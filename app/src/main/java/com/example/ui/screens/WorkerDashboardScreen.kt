package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ReferralEntity
import com.example.data.ReferralStatus
import com.example.data.TaskCategory
import com.example.data.TaskEntity
import com.example.data.WithdrawalEntity
import com.example.data.WithdrawalStatus
import com.example.data.WorkerEntity
import com.example.ui.components.ContactAdminButtons
import com.example.ui.components.PinkGradientBackground
import com.example.ui.components.PinkSurfaceCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.categoryIcon
import com.example.ui.components.copyToClipboard
import com.example.ui.components.formatDateTime
import com.example.ui.components.formatTaka
import com.example.ui.components.openUrlSafe
import com.example.ui.theme.InkBlack
import com.example.ui.theme.InkBlackMuted
import com.example.ui.theme.InkBlackSecondary
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PinkBgDeep
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.PinkCard
import com.example.ui.theme.PinkCardAlt
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.StatusAmberBg
import com.example.ui.theme.StatusAmberInk
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusGreenInk
import com.example.ui.theme.StatusRedBg
import com.example.ui.theme.StatusRedInk
import com.example.ui.viewmodel.WorkerViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerDashboardScreen(
    viewModel: WorkerViewModel,
    onSwitchPortal: () -> Unit,
    onLogout: () -> Unit
) {
    val worker by viewModel.worker.collectAsStateWithLifecycle()
    val config by viewModel.config.collectAsStateWithLifecycle()
    val tasks by viewModel.activeTasks.collectAsStateWithLifecycle()
    val completedIds by viewModel.completedTaskIds.collectAsStateWithLifecycle()
    val referrals by viewModel.referrals.collectAsStateWithLifecycle()
    val withdrawals by viewModel.withdrawals.collectAsStateWithLifecycle()
    val feedback by viewModel.feedbackMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var activeTaskForRunner by remember { mutableStateOf<TaskEntity?>(null) }

    LaunchedEffect(feedback) {
        feedback?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeFeedback()
        }
    }

    val currentWorker = worker ?: return

    val navItems = listOf(
        Triple("কাজ", Icons.Filled.TaskAlt, "worker_nav_tasks"),
        Triple("রেফার", Icons.Filled.CardGiftcard, "worker_nav_refer"),
        Triple("উত্তোলন", Icons.Filled.AccountBalanceWallet, "worker_nav_withdraw"),
        Triple("যোগাযোগ", Icons.Filled.SupportAgent, "worker_nav_support")
    )

    PinkGradientBackground {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useRail = maxWidth >= 600.dp

            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        modifier = Modifier.statusBarsPadding(),
                        title = {
                            Column {
                                Text(
                                    text = "BD Earning Team Pro",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = InkBlack,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${currentWorker.name} • ব্যালেন্স: ${formatTaka(currentWorker.balance)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = InkBlackSecondary
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = onSwitchPortal,
                                modifier = Modifier.testTag("worker_switch_portal_button")
                            ) {
                                Icon(Icons.Filled.SwapHoriz, contentDescription = "পোর্টাল", tint = InkBlack)
                            }
                            IconButton(
                                onClick = onLogout,
                                modifier = Modifier.testTag("worker_logout_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "লগআউট", tint = InkBlack)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = PinkBgDeep.copy(alpha = 0.7f))
                    )
                },
                bottomBar = {
                    if (!useRail) {
                        NavigationBar(
                            containerColor = PinkCard,
                            contentColor = InkBlack,
                            modifier = Modifier.navigationBarsPadding()
                        ) {
                            navItems.forEachIndexed { index, (label, icon, tag) ->
                                NavigationBarItem(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    icon = { Icon(icon, contentDescription = label) },
                                    label = { Text(label, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag(tag),
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = InkBlack,
                                        selectedTextColor = InkBlack,
                                        unselectedIconColor = InkBlackMuted,
                                        unselectedTextColor = InkBlackMuted,
                                        indicatorColor = PinkPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (useRail) {
                        NavigationRail(
                            containerColor = PinkCard,
                            contentColor = InkBlack,
                            modifier = Modifier
                                .fillMaxHeight()
                                .navigationBarsPadding()
                        ) {
                            Spacer(modifier = Modifier.height(12.dp))
                            navItems.forEachIndexed { index, (label, icon, tag) ->
                                NavigationRailItem(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    icon = { Icon(icon, contentDescription = label) },
                                    label = { Text(label, fontWeight = FontWeight.Bold) },
                                    modifier = Modifier.testTag(tag),
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = InkBlack,
                                        selectedTextColor = InkBlack,
                                        unselectedIconColor = InkBlackMuted,
                                        unselectedTextColor = InkBlackMuted,
                                        indicatorColor = PinkPrimary
                                    )
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Box(modifier = Modifier.widthIn(max = 680.dp).fillMaxSize()) {
                            when (selectedTab) {
                                0 -> WorkerTasksTab(
                                    worker = currentWorker,
                                    tasks = tasks,
                                    completedIds = completedIds.toSet(),
                                    onStartTask = { activeTaskForRunner = it }
                                )
                                1 -> WorkerReferTab(
                                    worker = currentWorker,
                                    referrals = referrals
                                )
                                2 -> WorkerWithdrawTab(
                                    worker = currentWorker,
                                    withdrawals = withdrawals,
                                    onRequestWithdrawal = { amount, method, account ->
                                        viewModel.requestWithdrawal(amount, method, account)
                                    }
                                )
                                3 -> WorkerSupportTab(
                                    worker = currentWorker,
                                    whatsappNumber = config.whatsappNumber,
                                    facebookUrl = config.facebookUrl,
                                    onLogout = onLogout
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    activeTaskForRunner?.let { task ->
        TaskRunnerDialog(
            task = task,
            onDismiss = { activeTaskForRunner = null },
            onClaimReward = {
                viewModel.completeTask(task.id)
                activeTaskForRunner = null
            }
        )
    }
}

@Composable
private fun WorkerTasksTab(
    worker: WorkerEntity,
    tasks: List<TaskEntity>,
    completedIds: Set<Long>,
    onStartTask: (TaskEntity) -> Unit
) {
    var selectedCategory by rememberSaveable { mutableStateOf<TaskCategory?>(null) }

    val filtered = remember(tasks, selectedCategory) {
        if (selectedCategory == null) tasks else tasks.filter { it.taskCategory() == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            PinkSurfaceCard(containerColor = PinkPrimary) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "মোট ব্যালেন্স",
                        style = MaterialTheme.typography.labelLarge,
                        color = InkBlackSecondary
                    )
                    Text(
                        text = formatTaka(worker.balance),
                        style = MaterialTheme.typography.displaySmall,
                        color = InkBlack,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "মোট আয়: ${formatTaka(worker.totalEarned)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkBlack,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "ভেরিফায়েড রেফার: ${worker.verifiedReferralsCount}/৩",
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkBlack,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("সব কাজ", color = InkBlack, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PinkPrimary,
                        containerColor = PinkCard
                    )
                )
                TaskCategory.entries.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.labelBn, color = InkBlack, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PinkPrimary,
                            containerColor = PinkCard
                        )
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                PinkSurfaceCard {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TaskAlt,
                            contentDescription = null,
                            tint = InkBlack,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "এখনও কোনো কাজ পাওয়া যায়নি",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = InkBlack
                        )
                        Text(
                            text = "অ্যাডমিন নতুন ভিডিও, বিজ্ঞাপন, গেম বা ফলো টাস্কের লিংক যোগ করলে এখানে দেখতে পাবেন।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkBlackSecondary
                        )
                    }
                }
            }
        } else {
            items(filtered, key = { it.id }) { task ->
                val isCompleted = task.id in completedIds
                val category = task.taskCategory()
                PinkSurfaceCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = categoryIcon(category),
                                    contentDescription = category.labelBn,
                                    tint = InkBlack,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = InkBlack,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${category.labelBn} • ${task.durationSeconds} সেকেন্ড",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = InkBlackMuted
                                    )
                                }
                            }
                            StatusBadge(
                                text = formatTaka(task.rewardAmount),
                                backgroundColor = if (isCompleted) StatusGreenBg else PinkPrimary,
                                textColor = if (isCompleted) StatusGreenInk else InkBlack
                            )
                        }

                        if (task.description.isNotBlank()) {
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkBlackSecondary
                            )
                        }

                        Button(
                            onClick = { onStartTask(task) },
                            enabled = !isCompleted,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCompleted) StatusGreenBg else PinkPrimary,
                                contentColor = InkBlack,
                                disabledContainerColor = StatusGreenBg,
                                disabledContentColor = StatusGreenInk
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("worker_start_task_${task.id}")
                        ) {
                            Icon(
                                imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Filled.PlayArrow,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isCompleted) "কাজ সম্পন্ন হয়েছে" else "কাজ শুরু করুন (${formatTaka(task.rewardAmount)})",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun TaskRunnerDialog(
    task: TaskEntity,
    onDismiss: () -> Unit,
    onClaimReward: () -> Unit
) {
    val context = LocalContext.current
    var linkOpened by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableIntStateOf(task.durationSeconds.coerceAtLeast(5)) }

    LaunchedEffect(linkOpened) {
        if (linkOpened && remainingSeconds > 0) {
            while (remainingSeconds > 0) {
                delay(1000L)
                remainingSeconds--
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PinkCard,
        titleContentColor = InkBlack,
        textContentColor = InkBlack,
        title = {
            Text(
                text = task.title,
                fontWeight = FontWeight.Bold,
                color = InkBlack
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "ধাপ ১: নিচের বাটনে ট্যাপ করে লিংকে যান (${task.taskCategory().labelBn})।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkBlackSecondary
                )
                OutlinedButton(
                    onClick = {
                        openUrlSafe(context, task.linkUrl)
                        linkOpened = true
                    },
                    border = BorderStroke(1.2.dp, PinkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("task_open_link_button")
                ) {
                    Icon(Icons.Filled.OpenInNew, contentDescription = null, tint = InkBlack)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("লিংক খুলুন", color = InkBlack, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "ধাপ ২: ${task.durationSeconds} সেকেন্ড অপেক্ষা করার পর পুরস্কার সংগ্রহ করুন।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkBlackSecondary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Timer, contentDescription = null, tint = InkBlack)
                    Text(
                        text = if (!linkOpened) {
                            "শুরু করতে 'লিংক খুলুন' ট্যাপ করুন"
                        } else if (remainingSeconds > 0) {
                            "বাকি আছে: $remainingSeconds সেকেন্ড"
                        } else {
                            "সময় সম্পন্ন হয়েছে! পুরস্কার নিন।"
                        },
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )
                }

                val progress = if (task.durationSeconds > 0) {
                    1f - (remainingSeconds.toFloat() / task.durationSeconds.toFloat())
                } else 1f
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                    color = PinkAccent,
                    trackColor = PinkBgDeep
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onClaimReward,
                enabled = linkOpened && remainingSeconds == 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PinkPrimary,
                    contentColor = InkBlack
                ),
                modifier = Modifier.testTag("task_claim_reward_button")
            ) {
                Text("পুরস্কার নিন (${formatTaka(task.rewardAmount)})", fontWeight = FontWeight.Bold, color = InkBlack)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = InkBlack, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun WorkerReferTab(
    worker: WorkerEntity,
    referrals: List<ReferralEntity>
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            PinkSurfaceCard(containerColor = PinkPrimary) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "প্রতি ভেরিফায়েড রেফারে ৫০ টাকা!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )
                    Text(
                        text = "বন্ধুকে আপনার রেফার কোড দিন এবং bdearning://worker লিংকে রেজিস্ট্রেশন করতে বলুন। অ্যাডমিন যাচাই করলে সাথে সাথে ৳৫০ পাবেন।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkBlackSecondary
                    )
                    Surface(
                        color = PinkCard,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, InkBlack.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("আপনার রেফার কোড", style = MaterialTheme.typography.labelMedium, color = InkBlackMuted)
                                Text(
                                    text = worker.referralCode,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = InkBlack
                                )
                            }
                            Row {
                                IconButton(
                                    onClick = { copyToClipboard(context, "রেফার কোড", worker.referralCode) },
                                    modifier = Modifier.testTag("copy_referral_code_button")
                                ) {
                                    Icon(Icons.Filled.ContentCopy, contentDescription = "কপি করুন", tint = InkBlack)
                                }
                                IconButton(
                                    onClick = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "BD Earning Team Pro অ্যাপে জয়েন করুন! আমার রেফার কোড ব্যবহার করুন: ${worker.referralCode}"
                                            )
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "রেফার কোড শেয়ার করুন"))
                                    },
                                    modifier = Modifier.testTag("share_referral_code_button")
                                ) {
                                    Icon(Icons.Filled.Share, contentDescription = "শেয়ার করুন", tint = InkBlack)
                                }
                            }
                        }
                    }
                    Text(
                        text = "মোট ভেরিফায়েড রেফার: ${worker.verifiedReferralsCount} টি (উত্তোলনের জন্য কমপক্ষে ৩টি প্রয়োজন)",
                        style = MaterialTheme.typography.labelLarge,
                        color = InkBlack
                    )
                }
            }
        }

        item {
            Text(
                text = "আপনার রেফার তালিকা (${referrals.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = InkBlack
            )
        }

        if (referrals.isEmpty()) {
            item {
                PinkSurfaceCard {
                    Text(
                        text = "এখনও কেউ আপনার রেফার কোড ব্যবহার করেনি।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkBlackSecondary,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        } else {
            items(referrals, key = { it.id }) { ref ->
                val status = ref.referralStatus()
                val (bg, ink) = when (status) {
                    ReferralStatus.VERIFIED -> StatusGreenBg to StatusGreenInk
                    ReferralStatus.PENDING -> StatusAmberBg to StatusAmberInk
                    ReferralStatus.REJECTED -> StatusRedBg to StatusRedInk
                }
                PinkSurfaceCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(ref.referredName, fontWeight = FontWeight.Bold, color = InkBlack)
                            Text(ref.referredPhone, style = MaterialTheme.typography.bodyMedium, color = InkBlackMuted)
                            Text(formatDateTime(ref.createdAt), style = MaterialTheme.typography.labelSmall, color = InkBlackMuted)
                        }
                        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            StatusBadge(text = status.labelBn, backgroundColor = bg, textColor = ink)
                            Text(formatTaka(ref.bonusAmount), fontWeight = FontWeight.Bold, color = InkBlack)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun WorkerWithdrawTab(
    worker: WorkerEntity,
    withdrawals: List<WithdrawalEntity>,
    onRequestWithdrawal: (amount: Double, method: String, account: String) -> Unit
) {
    var selectedMethod by rememberSaveable { mutableStateOf("bKash") }
    var accountNumber by rememberSaveable { mutableStateOf(worker.phone) }
    var amountText by rememberSaveable { mutableStateOf("100") }

    val hasEnoughReferrals = worker.verifiedReferralsCount >= 3
    val hasMinBalance = worker.balance >= 100.0
    val canWithdraw = hasEnoughReferrals && hasMinBalance

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = InkBlack,
        unfocusedTextColor = InkBlack,
        focusedBorderColor = PinkBorder,
        unfocusedBorderColor = InkBlack.copy(alpha = 0.35f),
        focusedLabelColor = InkBlack,
        unfocusedLabelColor = InkBlackSecondary,
        cursorColor = InkBlack
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            PinkSurfaceCard(containerColor = PinkCardAlt) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "টাকা তোলার শর্তাবলি",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("১. কমপক্ষে ৩টি ভেরিফায়েড রেফার", color = InkBlackSecondary)
                        StatusBadge(
                            text = "${worker.verifiedReferralsCount}/৩",
                            backgroundColor = if (hasEnoughReferrals) StatusGreenBg else StatusAmberBg,
                            textColor = if (hasEnoughReferrals) StatusGreenInk else StatusAmberInk
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("২. সর্বনিম্ন ১০০ টাকা ব্যালেন্স", color = InkBlackSecondary)
                        StatusBadge(
                            text = formatTaka(worker.balance),
                            backgroundColor = if (hasMinBalance) StatusGreenBg else StatusAmberBg,
                            textColor = if (hasMinBalance) StatusGreenInk else StatusAmberInk
                        )
                    }
                }
            }
        }

        item {
            PinkSurfaceCard {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "টাকা উত্তোলনের আবেদন",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("bKash", "Nagad", "Rocket").forEach { method ->
                            FilterChip(
                                selected = selectedMethod == method,
                                onClick = { selectedMethod = method },
                                label = { Text(method, color = InkBlack, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PinkPrimary,
                                    containerColor = PinkCardAlt
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = accountNumber,
                        onValueChange = { accountNumber = it },
                        label = { Text("$selectedMethod অ্যাকাউন্ট নম্বর") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_account_input")
                    )

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("পরিমাণ (সর্বনিম্ন ৳১০০)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_amount_input")
                    )

                    Button(
                        onClick = {
                            val parsed = amountText.trim().toDoubleOrNull() ?: 0.0
                            onRequestWithdrawal(parsed, selectedMethod, accountNumber)
                        },
                        enabled = canWithdraw,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PinkPrimary,
                            contentColor = InkBlack
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("withdraw_submit_button")
                    ) {
                        Text(
                            text = if (canWithdraw) "উত্তোলন রিকোয়েস্ট পাঠান" else "শর্ত পূরণ হয়নি",
                            fontWeight = FontWeight.Bold,
                            color = InkBlack
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "উত্তোলন ইতিহাস (${withdrawals.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = InkBlack
            )
        }

        items(withdrawals, key = { it.id }) { item ->
            val status = item.withdrawalStatus()
            val (bg, ink) = when (status) {
                WithdrawalStatus.APPROVED -> StatusGreenBg to StatusGreenInk
                WithdrawalStatus.PENDING -> StatusAmberBg to StatusAmberInk
                WithdrawalStatus.REJECTED -> StatusRedBg to StatusRedInk
            }
            PinkSurfaceCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${item.method} • ${item.accountNumber}",
                            fontWeight = FontWeight.Bold,
                            color = InkBlack
                        )
                        Text(
                            text = formatDateTime(item.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = InkBlackMuted
                        )
                        if (item.adminNote.isNotBlank()) {
                            Text(
                                text = item.adminNote,
                                style = MaterialTheme.typography.bodyMedium,
                                color = InkBlackSecondary
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        StatusBadge(text = status.labelBn, backgroundColor = bg, textColor = ink)
                        Text(formatTaka(item.amount), fontWeight = FontWeight.Bold, color = InkBlack)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun WorkerSupportTab(
    worker: WorkerEntity,
    whatsappNumber: String,
    facebookUrl: String,
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            PinkSurfaceCard {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("প্রোফাইল তথ্য", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = InkBlack)
                    Text("নাম: ${worker.name}", color = InkBlack)
                    Text("মোবাইল: ${worker.phone}", color = InkBlack)
                    Text("রেফার কোড: ${worker.referralCode}", color = InkBlack, fontWeight = FontWeight.Bold)
                    Text("মোট আয়: ${formatTaka(worker.totalEarned)}", color = InkBlack)
                }
            }
        }

        item {
            PinkSurfaceCard(containerColor = PinkCardAlt) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "অ্যাডমিন সাপোর্ট (WhatsApp ও Facebook)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )
                    Text(
                        text = "পেমেন্ট বা কাজের কোনো বিষয়ে সাহায্য লাগলে সরাসরি অ্যাডমিনকে মেসেজ করুন।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = InkBlackSecondary
                    )
                    ContactAdminButtons(
                        context = context,
                        whatsappNumber = whatsappNumber,
                        facebookUrl = facebookUrl
                    )
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onLogout,
                border = BorderStroke(1.2.dp, InkBlack),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("worker_profile_logout_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = InkBlack)
                Spacer(modifier = Modifier.width(8.dp))
                Text("লগআউট করুন", color = InkBlack, fontWeight = FontWeight.Bold)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
