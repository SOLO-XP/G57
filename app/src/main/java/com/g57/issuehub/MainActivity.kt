package com.g57.issuehub

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.g57.issuehub.data.Attachment
import com.g57.issuehub.data.Issue
import com.g57.issuehub.data.Notification
import com.g57.issuehub.data.Profile
import com.g57.issuehub.ui.*
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { G57App() }
    }
}

private val G57Bg = Color(0xFF080A0F)
private val G57Card = Color(0xFF121722)
private val G57Primary = Color(0xFF9B7BFF)
private val G57Cyan = Color(0xFF62D9FF)

@Composable
fun G57App(vm: G57ViewModel = viewModel()) {
    val ui by vm.ui.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(ui.openUrl) {
        ui.openUrl?.let { url ->
            runCatching {
                context.startActivity(Intent(Intent.ACTION_VIEW).apply { data = android.net.Uri.parse(url) })
            }
            vm.clearOpenUrl()
        }
    }

    BackHandler(enabled = ui.screen != ScreenState.Login) { vm.back() }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = G57Bg,
            surface = G57Card,
            primary = G57Primary,
            secondary = G57Cyan,
            onBackground = Color(0xFFF5F7FB),
            onSurface = Color(0xFFF5F7FB)
        )
    ) {
        Box(Modifier.fillMaxSize().background(G57Bg)) {
            when (val screen = ui.screen) {
                ScreenState.Login -> LoginScreen(ui, vm)
                ScreenState.UserHome -> UserHome(ui, vm)
                ScreenState.CreateIssue -> CreateIssueScreen(ui, vm)
                is ScreenState.UserIssue -> IssueDetails(screen.issue, ui, vm, admin = false)
                ScreenState.AdminHome -> AdminHome(ui, vm)
                ScreenState.AdminSolvedUsers -> AdminSolvedUsers(ui, vm)
                ScreenState.AdminUserManagement -> AdminUserManagementScreen(ui, vm)
                ScreenState.DriverDevelopers -> DriverDevelopersScreen(vm)
                is ScreenState.AdminIssue -> IssueDetails(screen.issue, ui, vm, admin = true)
            }
        }
    }
}

@Composable
private fun BrandHeader(subtitle: String? = null, onBack: (() -> Unit)? = null, onMenu: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
        if (onMenu != null) {
            IconButton(onClick = onMenu, modifier = Modifier.padding(end = 4.dp)) {
                Icon(Icons.Default.Menu, contentDescription = "Open menu", tint = G57Primary, modifier = Modifier.size(28.dp))
            }
        }
        if (onBack != null) {
            Surface(
                onClick = onBack,
                shape = RoundedCornerShape(14.dp),
                color = G57Primary,
                contentColor = Color(0xFF080A0F),
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text("BACK", fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        Column(Modifier.weight(1f)) {
            Text("G57", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = G57Primary)
            if (subtitle != null) Text(subtitle, color = Color.Gray)
        }
    }
}

@Composable
private fun LoginScreen(ui: UiState, vm: G57ViewModel) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painterResource(com.g57.issuehub.R.drawable.g57_logo), "G57 logo", Modifier.size(112.dp))
        Spacer(Modifier.height(12.dp))
        Text("G57", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold, color = G57Primary)
        Text("Mali GPU Issue Reporter", color = G57Cyan, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(24.dp))
        Text("Choose access", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            RoleCard("USER", "Report & track issues", ui.loginRole == "user", { vm.setLoginRole("user") }, Modifier.weight(1f))
            RoleCard("ADMIN", "Developer panel", ui.loginRole == "admin", { vm.setLoginRole("admin") }, Modifier.weight(1f))
        }
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            ui.username,
            vm::setUsername,
            label = { Text("Username") },
            supportingText = { Text(if (ui.loginRole == "user") "Choose a unique username" else "Admin username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (ui.loginRole == "user") {
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !ui.userCreateAccount,
                    onClick = { vm.setUserCreateAccount(false) },
                    label = { Text("SIGN IN") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = ui.userCreateAccount,
                    onClick = { vm.setUserCreateAccount(true) },
                    label = { Text("CREATE ACCOUNT") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            ui.password,
            vm::setPassword,
            label = { Text("Password") },
            supportingText = { Text(if (ui.loginRole == "user" && ui.userCreateAccount) "Use at least 8 characters. Keep it safe for reinstalling." else "Your account password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(18.dp))
        Button(onClick = vm::login, enabled = !ui.loading, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Text(if (ui.loading) "PLEASE WAIT…" else if (ui.loginRole == "user") { if (ui.userCreateAccount) "CREATE ACCOUNT" else "SIGN IN" } else "SIGN IN")
        }
        ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 14.dp)) }
    }
}

@Composable
private fun RoleCard(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Card(onClick = onClick, modifier = modifier, colors = CardDefaults.cardColors(containerColor = if (selected) G57Primary.copy(alpha = 0.22f) else G57Card), border = if (selected) androidx.compose.foundation.BorderStroke(1.dp, G57Primary) else null, shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(if (title == "ADMIN") Icons.Default.AdminPanelSettings else Icons.Default.Person, null, tint = if (selected) G57Primary else G57Cyan)
            Spacer(Modifier.height(6.dp))
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun UserHome(ui: UiState, vm: G57ViewModel) {
    var shownNotification by remember(ui.profile?.id, ui.notifications) {
        mutableStateOf(ui.notifications.firstOrNull { it.readAt == null })
    }
    LaunchedEffect(ui.profile?.id) {
        ui.profile?.id?.let(vm::loadUserData)
    }
    Column(Modifier.fillMaxSize()) {
        BrandHeader("Your issues", onMenu = vm::openDriverDevelopers)
        Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Welcome, ${ui.profile?.username ?: ui.username}", style = MaterialTheme.typography.titleLarge)
                val unread = ui.notifications.count { it.readAt == null }
                if (unread > 0) Text("🔔 $unread notification${if (unread == 1) "" else "s"}", color = G57Cyan)
            }
        }
        Spacer(Modifier.height(14.dp))
        Button(vm::openCreate, Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(18.dp)) {
            Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("CREATE ISSUE")
        }
        Spacer(Modifier.height(18.dp))
        IssueList(ui.issues, vm::openUserIssue, "You have no issues yet.")
    }

    shownNotification?.let { notification ->
        AlertDialog(
            onDismissRequest = {
                vm.markNotificationRead(notification)
                shownNotification = null
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.markNotificationRead(notification)
                    shownNotification = null
                }) { Text("OK") }
            },
            title = { Text("G57 • Problem Resolved ✅") },
            text = { Text("${notification.title}\n\n${notification.message}") }
        )
    }
}

@Composable
private fun AdminHome(ui: UiState, vm: G57ViewModel) {
    LaunchedEffect(Unit) { vm.loadIssues(true) }
    Column(Modifier.fillMaxSize()) {
        BrandHeader("ADMIN PANEL", onMenu = vm::openDriverDevelopers)
        Row(Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("All Issues", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Developer access", color = G57Cyan)
            }
            IconButton(onClick = vm::openUserManagement) {
                Icon(Icons.Default.ManageAccounts, contentDescription = "User management")
            }
            IconButton(onClick = vm::openSolvedUsers) {
                Icon(Icons.Default.Verified, contentDescription = "Solved users")
            }
            IconButton(vm::logout) { Icon(Icons.Default.Logout, null) }
        }
        Row(Modifier.padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatChip("OPEN", ui.issues.count { it.status == "open" })
            StatChip("ACTIVE", ui.issues.count { it.status == "investigating" || it.status == "fix_in_progress" })
            StatChip("TESTING", ui.issues.count { it.status == "testing" })
            StatChip("FIXED", ui.issues.count { it.status == "fixed" || it.status == "closed" })
        }
        Spacer(Modifier.height(12.dp))
        IssueList(ui.issues, vm::openAdminIssue, "No issues in Cloud.")
    }
}

@Composable
private fun AdminSolvedUsers(ui: UiState, vm: G57ViewModel) {
    LaunchedEffect(Unit) { vm.loadSolvedUsers() }
    Column(Modifier.fillMaxSize()) {
        BrandHeader("USERS WITH RESOLVED ISSUES") { vm.back() }
        if (ui.resolvedUsers.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No resolved users yet.", color = Color.Gray) }
        } else {
            LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(ui.resolvedUsers) { user ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = G57Card)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF63E6BE))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(user.username, fontWeight = FontWeight.Bold)
                                Text("${user.issueCount} resolved issue(s) • latest #${user.latestIssueNumber}", color = Color.Gray)
                                Text(user.latestTitle, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DriverDevelopersScreen(vm: G57ViewModel) {
    val repositories = listOf(
        "Noysz / panvk-g99-jm" to "https://github.com/Noysz/panvk-g99-jm",
        "mexicanbr0auth / mesa-panvk-g57" to "https://github.com/mexicanbr0auth/mesa-panvk-g57",
        "FristOneRR / FristOneRR-Panvk-Driver" to "https://github.com/FristOneRR/FristOneRR-Panvk-Driver"
    )
    Column(Modifier.fillMaxSize()) {
        BrandHeader("GitHub Devlopers All G57 Drivers") { vm.back() }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(repositories) { (name, url) ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = G57Card),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Code, contentDescription = null, tint = G57Cyan, modifier = Modifier.size(26.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(url, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { vm.openExternalUrl(url) }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("OPEN GITHUB")
                        }
                    }
                }
            }
        }
        Text(
            "This App Made By Jin woo { PanVK tester }",
            color = G57Primary,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 20.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun AdminUserManagementScreen(ui: UiState, vm: G57ViewModel) {
    var selectedUser by remember { mutableStateOf<Profile?>(null) }
    LaunchedEffect(Unit) { vm.loadUsers(); vm.loadIssues(true) }
    Column(Modifier.fillMaxSize()) {
        BrandHeader("USER MANAGEMENT") { vm.back() }
        Text(
            "Registered profiles • deleting a user removes their linked issues and notifications.",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )
        ui.success?.let { Text(it, color = G57Cyan, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) }
        ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) }
        if (ui.loading && ui.userProfiles.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (ui.userProfiles.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No profiles found.", color = Color.Gray) }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ui.userProfiles, key = { it.id }) { profile ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(containerColor = G57Card),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (profile.role == "admin") Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (profile.role == "admin") G57Primary else G57Cyan
                                )
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(profile.username, fontWeight = FontWeight.Bold)
                                    Text("Role: ${profile.role}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                                    Text("Issues: ${ui.issues.count { it.userId == profile.id }}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                                    profile.createdAt?.let { Text("Created: ${it.take(10)}", color = Color.Gray, style = MaterialTheme.typography.bodySmall) }
                                }
                            }
                            if (profile.role != "admin" && profile.id != ui.profile?.id) {
                                Spacer(Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = { selectedUser = profile },
                                    enabled = !ui.loading,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Default.DeleteForever, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("DELETE USER")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    selectedUser?.let { profile ->
        AlertDialog(
            onDismissRequest = { selectedUser = null },
            title = { Text("Delete ${profile.username}?") },
            text = { Text("This permanently deletes the Auth account, profile, issues, attachments metadata and notifications. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { selectedUser = null; vm.deleteUser(profile) }, enabled = !ui.loading) {
                    Text("DELETE", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { selectedUser = null }) { Text("CANCEL") } }
        )
    }
}

@Composable
private fun StatChip(name: String, count: Int) {
    Surface(shape = RoundedCornerShape(14.dp), color = G57Card) {
        Column(Modifier.padding(horizontal = 11.dp, vertical = 9.dp)) {
            Text(count.toString(), fontWeight = FontWeight.Bold)
            Text(name, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
private fun IssueList(issues: List<Issue>, onClick: (Issue) -> Unit, emptyText: String) {
    var selectedGroup by remember { mutableStateOf("All") }
    val groups = listOf(
        "All" to issues.size,
        "Opened" to issues.count { it.status == "open" },
        "Active" to issues.count { it.status in listOf("investigating", "fix_in_progress", "testing") },
        "Fixed" to issues.count { it.status == "fixed" },
        "Closed" to issues.count { it.status == "closed" }
    )
    val filtered = remember(issues, selectedGroup) {
        val matching = when (selectedGroup) {
            "Opened" -> issues.filter { it.status == "open" }
            "Active" -> issues.filter { it.status in listOf("investigating", "fix_in_progress", "testing") }
            "Fixed" -> issues.filter { it.status == "fixed" }
            "Closed" -> issues.filter { it.status == "closed" }
            else -> issues
        }
        matching.sortedWith(compareByDescending<Issue> { it.issueNumber })
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            groups.forEach { (name, count) ->
                FilterChip(
                    selected = selectedGroup == name,
                    onClick = { selectedGroup = name },
                    label = { Text("$name ($count)") },
                    leadingIcon = if (selectedGroup == name) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }
        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    if (issues.isEmpty()) emptyText else "No issues in ${selectedGroup.lowercase()} yet.",
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { issue -> IssueCard(issue, onClick) }
            }
        }
    }
}

@Composable
private fun IssueCard(issue: Issue, onClick: (Issue) -> Unit) {
    ElevatedCard(onClick = { onClick(issue) }, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = G57Card)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("#${issue.issueNumber}", color = G57Cyan, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(10.dp))
                StatusBadge(issue.status)
            }
            Spacer(Modifier.height(8.dp))
            Text(issue.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("${issue.game} • ${issue.problemType}", color = Color.Gray)
            if (issue.userUsername != null) Text("User: ${issue.userUsername}", color = G57Cyan, style = MaterialTheme.typography.bodySmall)
            Text("${issue.driver} • ${issue.emulator}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val label = status.replace('_', ' ').replaceFirstChar { it.titlecase(Locale.getDefault()) }
    AssistChip(onClick = {}, label = { Text(label) }, leadingIcon = { Icon(Icons.Default.Circle, null, Modifier.size(8.dp)) })
}

@Composable
private fun CreateIssueScreen(ui: UiState, vm: G57ViewModel) {
    val scroll = rememberScrollState()
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        vm.addFiles(readPickedFiles(uris, "image", context))
    }
    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        vm.addFiles(readPickedFiles(uris, "video", context))
    }
    val logPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        vm.addFiles(readPickedFiles(uris, "log", context))
    }

    Column(Modifier.fillMaxSize()) {
        BrandHeader("Create an issue") { vm.back() }
        Column(Modifier.fillMaxSize().verticalScroll(scroll).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("PROBLEM", color = G57Cyan, fontWeight = FontWeight.Bold)
            OutlinedTextField(ui.title, { vm.setField("title", it) }, label = { Text("Issue title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(ui.game, { vm.setField("game", it) }, label = { Text("Game") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(ui.gameVersion, { vm.setField("gameVersion", it) }, label = { Text("Game version (optional)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(ui.description, { vm.setField("description", it) }, label = { Text("Describe the problem") }, minLines = 6, modifier = Modifier.fillMaxWidth())
            Text("Problem type", color = Color.Gray)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 4.dp)) {
                listOf("Crash", "Black Screen", "Graphics", "Performance", "Vulkan Error", "Other").forEach { type ->
                    FilterChip(selected = ui.problemType == type, onClick = { vm.setProblemType(type) }, label = { Text(type) })
                }
            }
            Text("ENVIRONMENT", color = G57Cyan, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            OutlinedTextField(ui.driver, { vm.setField("driver", it) }, label = { Text("Driver") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(ui.emulator, { vm.setField("emulator", it) }, label = { Text("Emulator") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(ui.emulatorVersion, { vm.setField("emulatorVersion", it) }, label = { Text("Emulator version") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(ui.dxvk, { vm.setField("dxvk", it) }, label = { Text("DXVK") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(ui.wine, { vm.setField("wine", it) }, label = { Text("Wine") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(ui.proton, { vm.setField("proton", it) }, label = { Text("Proton") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(ui.vkd3d, { vm.setField("vkd3d", it) }, label = { Text("VKD3D") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(ui.box64, { vm.setField("box64", it) }, label = { Text("Box64") }, modifier = Modifier.fillMaxWidth())

            Text("ATTACHMENTS", color = G57Cyan, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            Text("Maximum 150 MB per file in this MVP.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { imagePicker.launch(arrayOf("image/*")) }, modifier = Modifier.weight(1f)) { Text("📷 Images") }
                OutlinedButton(onClick = { videoPicker.launch(arrayOf("video/*")) }, modifier = Modifier.weight(1f)) { Text("🎥 Video") }
                OutlinedButton(onClick = { logPicker.launch(arrayOf("text/*", "application/zip", "application/octet-stream", "application/json")) }, modifier = Modifier.weight(1f)) { Text("📄 Logs") }
            }
            ui.selectedFiles.forEachIndexed { index, file ->
                Surface(color = G57Card, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(file.name, Modifier.weight(1f), maxLines = 1)
                        Text(formatBytes(file.size), color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        IconButton(onClick = { vm.removeFile(index) }) { Icon(Icons.Default.Close, contentDescription = "Remove") }
                    }
                }
            }
            ui.success?.let { Text(it, color = G57Cyan) }
            ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(vm::submitIssue, enabled = !ui.loading, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) { Text(if (ui.loading) "UPLOADING…" else "SUBMIT ISSUE") }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun IssueDetails(issue: Issue, ui: UiState, vm: G57ViewModel, admin: Boolean) {
    var showDelete by remember { mutableStateOf(false) }
    var note by remember(issue.id) { mutableStateOf(issue.developerNote.orEmpty()) }
    var statusExpanded by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        BrandHeader(if (admin) "ADMIN • Issue #${issue.issueNumber}" else "Issue #${issue.issueNumber}") { vm.back() }
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(issue.status)
                Spacer(Modifier.weight(1f))
            }
            Text(issue.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Button(
                onClick = { showDelete = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7A2430),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (admin) "DELETE ISSUE (ADMIN)" else "DELETE MY ISSUE")
            }
            Text(issue.description)
            if (admin) Detail("User", issue.userUsername ?: issue.userId ?: "Unknown")
            Detail("Game", "${issue.game} ${issue.gameVersion.orEmpty()}")
            Detail("Problem", issue.problemType)
            Detail("Driver", issue.driver)
            Detail("Emulator", "${issue.emulator} ${issue.emulatorVersion.orEmpty()}")
            Detail("DXVK", issue.dxvk ?: "None")
            Detail("Wine", issue.wine ?: "None")
            Detail("Proton", issue.proton ?: "None")
            Detail("VKD3D", issue.vkd3d ?: "None")
            Detail("Box64", issue.box64 ?: "None")
            Detail("GPU", issue.gpu ?: "Unknown")
            Detail("SoC", issue.soc ?: "Unknown")
            Detail("Device", issue.deviceModel ?: "Unknown")
            Detail("Android", issue.androidVersion ?: "Unknown")

            Text("Attachments", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
            if (ui.attachments.isEmpty()) {
                Text("No attachments.", color = Color.Gray)
            } else {
                ui.attachments.forEach { attachment -> AttachmentRow(attachment) { vm.openAttachment(attachment.storagePath) } }
            }

            if (admin) {
                Text("DEVELOPER TOOLS", color = G57Cyan, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp))
                Box {
                    OutlinedButton(onClick = { statusExpanded = true }, modifier = Modifier.fillMaxWidth()) { Text("Status: ${issue.status.replace('_', ' ')} ▼") }
                    DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                        listOf("open", "investigating", "fix_in_progress", "testing", "fixed", "closed").forEach { status ->
                            DropdownMenuItem(text = { Text(status.replace('_', ' ').replaceFirstChar { it.titlecase(Locale.getDefault()) }) }, onClick = {
                                statusExpanded = false
                                vm.updateStatus(issue.id, status)
                            })
                        }
                    }
                }
                OutlinedTextField(note, { note = it }, label = { Text("Developer note") }, minLines = 4, modifier = Modifier.fillMaxWidth())
                Button(onClick = { vm.saveDeveloperNote(issue.id, note) }, modifier = Modifier.fillMaxWidth()) { Text("SAVE DEVELOPER NOTE") }
                Text("Setting status to Fixed/Closed creates an in-app notification for the user and adds them to Solved Users.", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }

            if (!admin && issue.status in listOf("fixed", "closed")) {
                Surface(color = Color(0xFF153322), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF63E6BE))
                        Spacer(Modifier.width(10.dp))
                        Text("The developer marked this issue as resolved.")
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            confirmButton = {
                TextButton(onClick = { showDelete = false; vm.deleteIssue(issue, admin) }) { Text("DELETE", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("CANCEL") } },
            title = { Text("Delete Issue #${issue.issueNumber}?") },
            text = { Text("This will delete the Issue record and its Cloud attachments. This action cannot be undone.") }
        )
    }
}

@Composable
private fun AttachmentRow(attachment: Attachment, onOpen: () -> Unit) {
    ElevatedCard(onClick = onOpen, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = G57Card)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            val icon = when (attachment.type) {
                "image" -> Icons.Default.Image
                "video" -> Icons.Default.VideoFile
                else -> Icons.Default.Description
            }
            Icon(icon, contentDescription = null, tint = G57Cyan)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(attachment.filename, maxLines = 1, fontWeight = FontWeight.SemiBold)
                Text("${attachment.type.uppercase(Locale.getDefault())} • ${formatBytes(attachment.size ?: 0)}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Default.OpenInNew, null)
        }
    }
}

@Composable
private fun Detail(name: String, value: String) {
    Surface(color = G57Card, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp)) { Text(name, Modifier.weight(0.35f), color = G57Cyan, fontWeight = FontWeight.Bold); Text(value, Modifier.weight(0.65f)) }
    }
}

private fun readPickedFiles(uris: List<android.net.Uri>, type: String, context: android.content.Context): List<PickedFile> {
    val result = mutableListOf<PickedFile>()
    uris.forEach { uri ->
        val mime = context.contentResolver.getType(uri) ?: "application/octet-stream"
        var name = "file"
        var size = 0L
        context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME, android.provider.OpenableColumns.SIZE), null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                if (nameIndex >= 0) name = cursor.getString(nameIndex) ?: name
                if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) size = cursor.getLong(sizeIndex)
            }
        }
        if (size <= 150L * 1024L * 1024L) result += PickedFile(uri, name, mime, type, size)
    }
    return result
}

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1024L * 1024L -> "%.1f MB".format(Locale.US, bytes / 1024f / 1024f)
    bytes >= 1024L -> "%.0f KB".format(Locale.US, bytes / 1024f)
    else -> "$bytes B"
}
