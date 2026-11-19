package com.example

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.data.EarningRepository
import com.example.data.SessionPreferences
import com.example.ui.screens.AdminAuthScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.PortalGateScreen
import com.example.ui.screens.WorkerAuthScreen
import com.example.ui.screens.WorkerDashboardScreen
import com.example.ui.screens.WorkerLockedScreen
import com.example.ui.theme.BDEarningTheme
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.PortalViewModel
import com.example.ui.viewmodel.WorkerViewModel
import kotlinx.coroutines.flow.MutableStateFlow

private const val ROUTE_PORTAL_GATE = "portal_gate"
private const val ROUTE_ADMIN_PORTAL = "admin_portal"
private const val ROUTE_WORKER_PORTAL = "worker_portal"

class MainActivity : ComponentActivity() {
    private val deepLinkRouteFlow = MutableStateFlow<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        handleDeepLink(intent)

        setContent {
            BDEarningTheme {
                BDEarningAppRoot(deepLinkRouteFlow = deepLinkRouteFlow)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val data = intent?.data ?: return
        if (data.scheme == "bdearning") {
            when (data.host?.lowercase()) {
                "admin" -> deepLinkRouteFlow.value = ROUTE_ADMIN_PORTAL
                "worker" -> deepLinkRouteFlow.value = ROUTE_WORKER_PORTAL
            }
        }
    }
}

@Composable
fun BDEarningAppRoot(deepLinkRouteFlow: MutableStateFlow<String?>) {
    val context = LocalContext.current.applicationContext
    val repository = remember(context) {
        val db = AppDatabase.getInstance(context)
        val prefs = SessionPreferences(context)
        EarningRepository(db.appDao(), prefs)
    }

    val portalViewModel: PortalViewModel = viewModel(
        factory = PortalViewModel.Factory(repository)
    )

    val config by portalViewModel.config.collectAsStateWithLifecycle()
    val session by portalViewModel.session.collectAsStateWithLifecycle()
    val activeWorker by portalViewModel.activeWorker.collectAsStateWithLifecycle()
    val authState by portalViewModel.authState.collectAsStateWithLifecycle()
    val deepLinkRoute by deepLinkRouteFlow.collectAsStateWithLifecycle()

    val navController = rememberNavController()

    LaunchedEffect(deepLinkRoute) {
        val target = deepLinkRoute ?: return@LaunchedEffect
        navController.navigate(target) {
            launchSingleTop = true
        }
        deepLinkRouteFlow.value = null
    }

    NavHost(
        navController = navController,
        startDestination = ROUTE_PORTAL_GATE
    ) {
        composable(ROUTE_PORTAL_GATE) {
            PortalGateScreen(
                config = config,
                onOpenWorkerPortal = {
                    navController.navigate(ROUTE_WORKER_PORTAL) { launchSingleTop = true }
                },
                onOpenAdminPortal = {
                    navController.navigate(ROUTE_ADMIN_PORTAL) { launchSingleTop = true }
                }
            )
        }

        composable(ROUTE_ADMIN_PORTAL) {
            if (!session.isAdminLoggedIn) {
                AdminAuthScreen(
                    config = config,
                    authState = authState,
                    onSetupAdmin = { name, phone, pass, wa, fb ->
                        portalViewModel.setupAdmin(name, phone, pass, wa, fb)
                    },
                    onLoginAdmin = { phone, pass ->
                        portalViewModel.loginAdmin(phone, pass)
                    },
                    onClearAuthState = { portalViewModel.clearAuthState() },
                    onBack = { navController.popBackStack() }
                )
            } else {
                val adminViewModel: AdminViewModel = viewModel(
                    key = "admin_session",
                    factory = AdminViewModel.Factory(repository)
                )
                AdminDashboardScreen(
                    viewModel = adminViewModel,
                    onSwitchPortal = {
                        navController.navigate(ROUTE_PORTAL_GATE) {
                            popUpTo(ROUTE_PORTAL_GATE) { inclusive = true }
                        }
                    },
                    onLogout = { portalViewModel.logoutAdmin() }
                )
            }
        }

        composable(ROUTE_WORKER_PORTAL) {
            val currentWorker = activeWorker
            if (currentWorker == null) {
                WorkerAuthScreen(
                    authState = authState,
                    onLogin = { phone, pass -> portalViewModel.loginWorker(phone, pass) },
                    onRegister = { name, phone, pass, ref ->
                        portalViewModel.registerWorker(name, phone, pass, ref)
                    },
                    onClearAuthState = { portalViewModel.clearAuthState() },
                    onBack = { navController.popBackStack() }
                )
            } else if (!currentWorker.isUnlocked) {
                WorkerLockedScreen(
                    worker = currentWorker,
                    config = config,
                    onSwitchToPortalGate = {
                        navController.navigate(ROUTE_PORTAL_GATE) {
                            popUpTo(ROUTE_PORTAL_GATE) { inclusive = true }
                        }
                    },
                    onLogout = { portalViewModel.logoutWorker() }
                )
            } else {
                key(currentWorker.id) {
                    val workerViewModel: WorkerViewModel = viewModel(
                        key = "worker_${currentWorker.id}",
                        factory = WorkerViewModel.Factory(currentWorker.id, repository)
                    )
                    WorkerDashboardScreen(
                        viewModel = workerViewModel,
                        onSwitchPortal = {
                            navController.navigate(ROUTE_PORTAL_GATE) {
                                popUpTo(ROUTE_PORTAL_GATE) { inclusive = true }
                            }
                        },
                        onLogout = { portalViewModel.logoutWorker() }
                    )
                }
            }
        }
    }
}
