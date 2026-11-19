package com.example.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.TaskCategory
import com.example.data.TaskEntity
import com.example.data.WorkerEntity
import com.example.ui.components.PinkGradientBackground
import com.example.ui.components.PinkSurfaceCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.categoryIcon
import com.example.ui.components.formatTaka
import com.example.ui.theme.InkBlack
import com.example.ui.theme.InkBlackMuted
import com.example.ui.theme.InkBlackSecondary
import com.example.ui.theme.PinkBgDeep
import com.example.ui.theme.PinkBorder
import com.example.ui.theme.PinkCard
import com.example.ui.theme.PinkPrimary
import com.example.ui.theme.StatusAmberBg
import com.example.ui.theme.StatusAmberInk
import com.example.ui.theme.StatusGreenBg
import com.example.ui.theme.StatusGreenInk
import com.example.ui.theme.StatusRedInk
import com.example.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onSwitchPortal: () -> Unit,
    onLogout: () -> Unit
) {
    val config by viewModel.config.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val workers by viewModel.workers.collectAsStateWithLifecycle()
    val referrals by viewModel.referrals.collectAsStateWithLifecycle()
    val withdrawals by viewModel.withdrawals.collectAsStateWithLifecycle()
    val feedback by viewModel.feedbackMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    LaunchedEffect(feedback) {
        feedback?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeFeedback()
        }
    }

    val navItems = listOf(
        Triple("কাজ যোগ", Icons.Filled.Task, "admin_nav_tasks"),
        Triple("কর্মী", Icons.Filled.Group, "admin_nav_workers"),
        Triple("পেমেন্ট", Icons.Filled.AccountBalance, "admin_nav_finance"),
        Triple("সেটিংস", Icons.Filled.Settings, "admin_nav_settings")
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
                                    text = "অ্যাডমিন প্যানেল",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = InkBlack,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${config.adminName.ifBlank { "Admin" }} • মোট কর্মী: ${workers.size} • টাস্ক: ${tasks.size}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = InkBlackSecondary
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = onSwitchPortal,
                                modifier = Modifier.testTag("admin_switch_portal_button")
                            ) {
                                Icon(Icons.Filled.SwapHoriz, contentDescription = "পোর্টাল", tint = InkBlack)
                            }
                            IconButton(
                                onClick = onLogout,
                                modifier = Modifier.testTag("admin_logout_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "লগআউট", tint = InkBlack)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = PinkBgDeep.copy(alpha = 0.7f))
                    )
                },
                floatingActionButton = {
                    if (selectedTab == 0) {
                        FloatingActionButton(
                            onClick = { showAddTaskDialog = true },
                            containerColor = PinkPrimary,
                            contentColor = InkBlack,
                            modifier = Modifier
                                .navigationBarsPadding()
                                .testTag("admin_add_task_fab")
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "নতুন কাজ যোগ করুন")
                        }
                    }
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
                                0 -> AdminTasksTab(
                                    tasks = tasks,
                                    onOpenAddDialog = { showAddTaskDialog = true },
                                    onToggleActive = { id, active -> viewModel.toggleTaskActive(id, active) },
                                    onDeleteTask = { id -> viewModel.deleteTask(id) }
                                )
                                1 -> AdminWorkersTab(
                                    workers = workers,
                                    onToggleUnlock = { id, unlocked -> viewModel.setWorkerUnlocked(id, unlocked) }
                                )
                                2 -> AdminFinanceTab(
                                    referrals = referrals,
                                    withdrawals = withdrawals,
                                    onVerifyReferral = { viewModel.verifyReferral(it) },
                                    onRejectReferral = { viewModel.rejectReferral(it) },
                                    onApproveWithdrawal = { viewModel.approveWithdrawal(it) },
                                    onRejectWithdrawal = { id, reason -> viewModel.rejectWithdrawal(id, reason) }
                                )
                                3 -> AdminSettingsTab(
                                    config = config,
                                    onSaveSettings = { wa, fb, pass -> viewModel.updateContactAndPassword(wa, fb, pass) },
                                    onLogout = onLogout
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onAddTask = { category, title, desc, url, reward, duration ->
                viewModel.addTask(category, title, desc, url, reward, duration)
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
private fun AdminTasksTab(
    tasks: List<TaskEntity>,
    onOpenAddDialog: () -> Unit,
    onToggleActive: (Long, Boolean) -> Unit,
    onDeleteTask: (Long) -> Unit
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("সব (${tasks.size})", color = InkBlack, fontWeight = FontWeight.Bold) },
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
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "কোনো ভিডিও, এড, গেম বা ফলো লিংক যোগ করা হয়নি",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = InkBlack
                        )
                        Text(
                            text = "নিচের '+' বাটনে ট্যাপ করে আপনার নিজস্ব লিংক ও পুরস্কার (সর্বোচ্চ ৳১০) যোগ করুন।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkBlackSecondary
                        )
                        Button(
                            onClick = onOpenAddDialog,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PinkPrimary,
                                contentColor = InkBlack
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("empty_add_task_button")
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("নতুন কাজ যোগ করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(filtered, key = { it.id }) { task ->
                val cat = task.taskCategory()
                PinkSurfaceCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = categoryIcon(cat),
                                    contentDescription = cat.labelBn,
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
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${cat.labelBn} • ${formatTaka(task.rewardAmount)} • ${task.durationSeconds}সে.",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = InkBlackMuted
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onDeleteTask(task.id) },
                                modifier = Modifier.testTag("admin_delete_task_${task.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "মুছে ফেলুন",
                                    tint = StatusRedInk
                                )
                            }
                        }

                        Text(
                            text = task.linkUrl,
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkBlackSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (task.isActive) "কর্মীদের জন্য চালু আছে" else "বন্ধ রাখা আছে",
                                style = MaterialTheme.typography.labelMedium,
                                color = InkBlack
                            )
                            Switch(
                                checked = task.isActive,
                                onCheckedChange = { onToggleActive(task.id, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = InkBlack,
                                    checkedTrackColor = PinkPrimary
                                ),
                                modifier = Modifier.testTag("admin_toggle_task_${task.id}")
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun AdminWorkersTab(
    workers: List<WorkerEntity>,
    onToggleUnlock: (Long, Boolean) -> Unit
) {
    var filterMode by rememberSaveable { mutableIntStateOf(0) } // 0=All, 1=Locked, 2=Unlocked

    val filtered = remember(workers, filterMode) {
        when (filterMode) {
            1 -> workers.filter { !it.isUnlocked }
            2 -> workers.filter { it.isUnlocked }
            else -> workers
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = filterMode == 0,
                    onClick = { filterMode = 0 },
                    label = { Text("সব (${workers.size})", color = InkBlack, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PinkPrimary, containerColor = PinkCard)
                )
                FilterChip(
                    selected = filterMode == 1,
                    onClick = { filterMode = 1 },
                    label = {
                        Text(
                            "লক (${workers.count { !it.isUnlocked }})",
                            color = InkBlack,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PinkPrimary, containerColor = PinkCard)
                )
                FilterChip(
                    selected = filterMode == 2,
                    onClick = { filterMode = 2 },
                    label = {
                        Text(
                            "আনলক (${workers.count { it.isUnlocked }})",
                            color = InkBlack,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PinkPrimary, containerColor = PinkCard)
                )
            }
        }

        if (filtered.isEmpty()) {
            item {
                PinkSurfaceCard {
                    Text(
                        text = "এই তালিকায় এখনও কোনো কর্মী নেই।",
                        color = InkBlackSecondary,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        } else {
            items(filtered, key = { it.id }) { worker ->
                PinkSurfaceCard {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = worker.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = InkBlack
                                )
                                Text(
                                    text = "${worker.phone} • কোড: ${worker.referralCode}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = InkBlackSecondary
                                )
                                Text(
                                    text = "ব্যালেন্স: ${formatTaka(worker.balance)} • ভেরিফায়েড রেফার: ${worker.verifiedReferralsCount}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = InkBlackMuted
                                )
                            }
                            StatusBadge(
                                text = if (worker.isUnlocked) "আনলকড" else "লকড",
                                backgroundColor = if (worker.isUnlocked) StatusGreenBg else StatusAmberBg,
                                textColor = if (worker.isUnlocked) StatusGreenInk else StatusAmberInk
                            )
                        }

                        Button(
                            onClick = { onToggleUnlock(worker.id, !worker.isUnlocked) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (worker.isUnlocked) StatusAmberBg else StatusGreenBg,
                                contentColor = if (worker.isUnlocked) StatusAmberInk else StatusGreenInk
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("admin_unlock_worker_${worker.id}")
                        ) {
                            Icon(
                                imageVector = if (worker.isUnlocked) Icons.Filled.Lock else Icons.Filled.LockOpen,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (worker.isUnlocked) "অ্যাকাউন্ট লক করুন" else "অ্যাকাউন্ট আনলক করুন",
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
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (
        category: TaskCategory,
        title: String,
        description: String,
        linkUrl: String,
        rewardAmount: Double,
        durationSeconds: Int
    ) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(TaskCategory.VIDEO) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var linkUrl by remember { mutableStateOf("") }
    var rewardText by remember { mutableStateOf("10") }
    var durationText by remember { mutableStateOf("15") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = InkBlack,
        unfocusedTextColor = InkBlack,
        focusedBorderColor = PinkBorder,
        unfocusedBorderColor = InkBlack.copy(alpha = 0.35f),
        focusedLabelColor = InkBlack,
        unfocusedLabelColor = InkBlackSecondary,
        cursorColor = InkBlack
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PinkCard,
        titleContentColor = InkBlack,
        textContentColor = InkBlack,
        title = {
            Text("নতুন কাজ / লিংক যোগ করুন", fontWeight = FontWeight.Bold, color = InkBlack)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaskCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.labelBn, color = InkBlack, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PinkPrimary,
                                containerColor = PinkBgDeep
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("কাজের নাম / শিরোনাম") },
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_task_title_input")
                )

                OutlinedTextField(
                    value = linkUrl,
                    onValueChange = { linkUrl = it },
                    label = { Text("লিংক (Video / Ad / Game / Follow URL)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    singleLine = true,
                    colors = fieldColors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_task_url_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("নির্দেশনা (ঐচ্ছিক)") },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = rewardText,
                        onValueChange = { rewardText = it },
                        label = { Text("পুরস্কার (সর্বোচ্চ ৳১০)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_task_reward_input")
                    )

                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it },
                        label = { Text("সময় (সেকেন্ড)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = fieldColors,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_task_duration_input")
                    )
                }

                errorMessage?.let {
                    Text(it, color = StatusRedInk, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val reward = rewardText.trim().toDoubleOrNull()
                    val duration = durationText.trim().toIntOrNull()
                    when {
                        title.isBlank() -> errorMessage = "কাজের শিরোনাম লিখুন"
                        linkUrl.isBlank() -> errorMessage = "লিংক লিখুন"
                        reward == null || reward < 1.0 || reward > 10.0 ->
                            errorMessage = "পুরস্কার ১ থেকে সর্বোচ্চ ১০ টাকার মধ্যে হতে হবে"
                        duration == null || duration < 5 || duration > 300 ->
                            errorMessage = "সময় ৫ থেকে ৩০০ সেকেন্ডের মধ্যে দিন"
                        else -> onAddTask(selectedCategory, title, description, linkUrl, reward, duration)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary, contentColor = InkBlack),
                modifier = Modifier.testTag("confirm_add_task_button")
            ) {
                Text("যোগ করুন", fontWeight = FontWeight.Bold, color = InkBlack)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = InkBlack, fontWeight = FontWeight.Bold)
            }
        }
    )
}
