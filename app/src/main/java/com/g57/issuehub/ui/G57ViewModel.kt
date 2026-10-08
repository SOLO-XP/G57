package com.g57.issuehub.ui

import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.g57.issuehub.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ScreenState {
    data object Login : ScreenState
    data object UserHome : ScreenState
    data object CreateIssue : ScreenState
    data class UserIssue(val issue: Issue) : ScreenState
    data object AdminHome : ScreenState
    data object AdminSolvedUsers : ScreenState
    data class AdminIssue(val issue: Issue) : ScreenState
}

data class PickedFile(
    val uri: Uri,
    val name: String,
    val mime: String,
    val type: String,
    val size: Long
)

data class ResolvedUser(
    val username: String,
    val issueCount: Int,
    val latestIssueNumber: Int,
    val latestTitle: String
)

data class UiState(
    val screen: ScreenState = ScreenState.Login,
    val username: String = "",
    val password: String = "",
    val loginRole: String = "user",
    val loading: Boolean = false,
    val error: String? = null,
    val success: String? = null,
    val profile: Profile? = null,
    val issues: List<Issue> = emptyList(),
    val attachments: List<Attachment> = emptyList(),
    val notifications: List<Notification> = emptyList(),
    val resolvedUsers: List<ResolvedUser> = emptyList(),
    val selectedFiles: List<PickedFile> = emptyList(),
    val openUrl: String? = null,
    val title: String = "",
    val description: String = "",
    val problemType: String = "Crash",
    val game: String = "",
    val driver: String = "Mali 26.2",
    val emulator: String = "Winlator",
    val emulatorVersion: String = "",
    val gameVersion: String = "",
    val dxvk: String = "",
    val wine: String = "",
    val proton: String = "",
    val vkd3d: String = "",
    val box64: String = ""
)

class G57ViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = G57Repository()
    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui.asStateFlow()

    init {
        restoreSession()
    }

    private fun restoreSession() = viewModelScope.launch {
        if (!SupabaseProvider.enabled) return@launch
        runCatching {
            val user = SupabaseProvider.client.auth.currentUserOrNull() ?: return@runCatching null
            repo.profile(user.id)
        }.onSuccess { profile ->
            profile ?: return@onSuccess
            update {
                copy(
                    username = profile.username,
                    profile = profile,
                    screen = if (profile.role == "admin") ScreenState.AdminHome else ScreenState.UserHome
                )
            }
            if (profile.role == "admin") loadIssues(true) else loadUserData(profile.id)
        }
    }

    fun setUsername(v: String) = update { copy(username = v, error = null) }
    fun setPassword(v: String) = update { copy(password = v, error = null) }
    fun setField(field: String, v: String) = update {
        when (field) {
            "title" -> copy(title = v)
            "description" -> copy(description = v)
            "game" -> copy(game = v)
            "driver" -> copy(driver = v)
            "emulator" -> copy(emulator = v)
            "emulatorVersion" -> copy(emulatorVersion = v)
            "gameVersion" -> copy(gameVersion = v)
            "dxvk" -> copy(dxvk = v)
            "wine" -> copy(wine = v)
            "proton" -> copy(proton = v)
            "vkd3d" -> copy(vkd3d = v)
            "box64" -> copy(box64 = v)
            else -> this
        }
    }
    fun setProblemType(v: String) = update { copy(problemType = v) }

    fun login() = viewModelScope.launch {
        val u = _ui.value.username.trim()
        val p = _ui.value.password
        val requestedRole = _ui.value.loginRole

        if (u.isBlank()) {
            update { copy(error = "Enter a username.") }
            return@launch
        }
        if (requestedRole == "admin" && p.isBlank()) {
            update { copy(error = "Enter the admin password.") }
            return@launch
        }
        if (!SupabaseProvider.enabled) {
            update { copy(error = "Cloud database is not configured in this build.") }
            return@launch
        }

        update { copy(loading = true, error = null, success = null) }

        runCatching {
            if (requestedRole == "admin") repo.signInAdmin(u, p)
            else repo.enterUser(u)
        }.onSuccess { profile ->
            if (profile.role != requestedRole) {
                runCatching { repo.signOut() }
                update { copy(loading = false, error = "This account is not registered as $requestedRole.") }
                return@onSuccess
            }
            update {
                copy(
                    loading = false,
                    password = "",
                    profile = profile,
                    screen = if (profile.role == "admin") ScreenState.AdminHome else ScreenState.UserHome
                )
            }
            if (profile.role == "admin") loadIssues(true) else loadUserData(profile.id)
        }.onFailure { e ->
            val raw = e.message.orEmpty()
            val friendly = if (requestedRole == "user" &&
                (raw.contains("duplicate", true) || raw.contains("unique", true) || raw.contains("already", true) || raw.contains("taken", true))
            ) "Username already taken. Choose another username."
            else raw.ifBlank { "Sign in failed" }
            runCatching {
                if (requestedRole == "user") repo.signOut()
            }
            update { copy(loading = false, error = friendly) }
        }
    }

    fun loadUserData(userId: String) = viewModelScope.launch {
        if (!SupabaseProvider.enabled) {
            update { copy(issues = emptyList(), notifications = emptyList()) }
            return@launch
        }
        update { copy(loading = true, error = null) }
        runCatching {
            val issues = repo.myIssues()
            val notifications = repo.notifications(userId)
            issues to notifications
        }.onSuccess { (issues, notifications) ->
            update { copy(loading = false, issues = issues, notifications = notifications) }
        }.onFailure { e -> update { copy(loading = false, error = e.message) } }
    }

    fun loadIssues(admin: Boolean) = viewModelScope.launch {
        if (!SupabaseProvider.enabled) {
            update { copy(issues = demoIssues()) }
            return@launch
        }
        update { copy(loading = true, error = null) }
        runCatching { if (admin) repo.allIssues() else repo.myIssues() }
            .onSuccess { list -> update { copy(loading = false, issues = list) } }
            .onFailure { e -> update { copy(loading = false, error = e.message) } }
    }

    fun openSolvedUsers() = update { copy(screen = ScreenState.AdminSolvedUsers, error = null) }

    fun loadSolvedUsers() = viewModelScope.launch {
        if (!SupabaseProvider.enabled) {
            update { copy(resolvedUsers = listOf(ResolvedUser("demo-user", 2, 481, "Black textures in Call of Duty"))) }
            return@launch
        }
        update { copy(loading = true, error = null) }
        runCatching {
            val profiles = repo.allProfiles().associateBy { it.id }
            val solved = repo.resolvedIssues()
            solved.groupBy { it.userId.orEmpty() }.mapNotNull { (userId, userIssues) ->
                val sorted = userIssues.sortedByDescending { it.createdAt.orEmpty() }
                val latest = sorted.firstOrNull() ?: return@mapNotNull null
                ResolvedUser(
                    username = profiles[userId]?.username ?: "user-${userId.take(8)}",
                    issueCount = userIssues.size,
                    latestIssueNumber = latest.issueNumber,
                    latestTitle = latest.title
                )
            }.sortedBy { it.username.lowercase() }
        }.onSuccess { list -> update { copy(loading = false, resolvedUsers = list) } }
         .onFailure { e -> update { copy(loading = false, error = e.message) } }
    }

    fun openCreate() = update {
        copy(
            screen = ScreenState.CreateIssue,
            error = null,
            success = null,
            selectedFiles = emptyList()
        )
    }

    fun addFiles(files: List<PickedFile>) = update {
        val existing = selectedFiles.map { it.uri }.toSet()
        copy(selectedFiles = selectedFiles + files.filter { it.uri !in existing })
    }

    fun removeFile(index: Int) = update {
        copy(selectedFiles = selectedFiles.toMutableList().apply { removeAt(index) })
    }

    fun submitIssue() = viewModelScope.launch {
        val s = _ui.value
        if (s.title.isBlank() || s.game.isBlank() || s.description.isBlank()) {
            update { copy(error = "Title, game, and description are required.") }
            return@launch
        }
        if (!SupabaseProvider.enabled) {
            update {
                copy(
                    loading = false,
                    success = "Demo issue created locally.",
                    screen = ScreenState.UserHome,
                    selectedFiles = emptyList(),
                    issues = demoIssues()
                )
            }
            return@launch
        }
        update { copy(loading = true, error = null, success = null) }
        runCatching {
            val issue = repo.createIssue(
                CreateIssueInput(
                    userId = s.profile?.id ?: error("Not authenticated"),
                    title = s.title,
                    description = s.description,
                    problemType = s.problemType,
                    game = s.game,
                    gameVersion = s.gameVersion.ifBlank { null },
                    driver = s.driver,
                    emulator = s.emulator,
                    emulatorVersion = s.emulatorVersion.ifBlank { null },
                    dxvk = s.dxvk.ifBlank { null },
                    wine = s.wine.ifBlank { null },
                    proton = s.proton.ifBlank { null },
                    vkd3d = s.vkd3d.ifBlank { null },
                    box64 = s.box64.ifBlank { null },
                    gpu = "Mali-G57 MC2",
                    soc = "Unknown",
                    androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                    deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
                )
            )
            uploadFiles(issue.id, s.selectedFiles)
            issue
        }.onSuccess { issue ->
            update {
                copy(
                    loading = false,
                    screen = ScreenState.UserHome,
                    success = "Issue #${issue.issueNumber} submitted.",
                    selectedFiles = emptyList(),
                    title = "",
                    description = "",
                    game = ""
                )
            }
            _ui.value.profile?.id?.let(::loadUserData)
        }.onFailure { e -> update { copy(loading = false, error = e.message ?: "Submission failed") } }
    }

    private suspend fun uploadFiles(issueId: String, files: List<PickedFile>) {
        val resolver = getApplication<Application>().contentResolver
        for (file in files) {
            resolver.openInputStream(file.uri)?.use { input ->
                repo.uploadAttachment(issueId, file.name, file.mime, input.readBytes(), file.type)
            } ?: error("Unable to read ${file.name}")
        }
    }

    fun openUserIssue(issue: Issue) = openIssue(issue, admin = false)
    fun openAdminIssue(issue: Issue) = openIssue(issue, admin = true)

    private fun openIssue(issue: Issue, admin: Boolean) = viewModelScope.launch {
        update {
            copy(
                screen = if (admin) ScreenState.AdminIssue(issue) else ScreenState.UserIssue(issue),
                attachments = emptyList(),
                loading = true,
                error = null
            )
        }
        if (!SupabaseProvider.enabled) {
            update { copy(loading = false) }
            return@launch
        }
        runCatching { repo.attachments(issue.id) }
            .onSuccess { files -> update { copy(loading = false, attachments = files) } }
            .onFailure { e -> update { copy(loading = false, error = e.message) } }
    }

    fun updateStatus(issueId: String, status: String) = viewModelScope.launch {
        if (!SupabaseProvider.enabled) {
            updateIssueInMemory(issueId, status)
            return@launch
        }
        update { copy(loading = true, error = null) }
        runCatching { repo.updateStatus(issueId, status) }
            .onSuccess {
                updateIssueInMemory(issueId, status)
                update { copy(loading = false, success = "Status updated") }
                loadIssues(true)
            }
            .onFailure { e -> update { copy(loading = false, error = e.message) } }
    }

    fun saveDeveloperNote(issueId: String, note: String) = viewModelScope.launch {
        if (!SupabaseProvider.enabled) {
            update { copy(success = "Note saved in demo mode") }
            return@launch
        }
        runCatching { repo.updateDeveloperNote(issueId, note) }
            .onSuccess {
                update { copy(success = "Developer note saved") }
            }
            .onFailure { e -> update { copy(error = e.message) } }
    }

    fun deleteIssue(issue: Issue, admin: Boolean) = viewModelScope.launch {
        if (!SupabaseProvider.enabled) {
            update { copy(issues = issues.filterNot { it.id == issue.id }, screen = if (admin) ScreenState.AdminHome else ScreenState.UserHome, success = "Issue deleted") }
            return@launch
        }
        update { copy(loading = true, error = null) }
        runCatching { repo.deleteIssue(issue.id) }
            .onSuccess {
                update {
                    copy(
                        loading = false,
                        issues = issues.filterNot { it.id == issue.id },
                        screen = if (admin) ScreenState.AdminHome else ScreenState.UserHome,
                        success = "Issue #${issue.issueNumber} deleted."
                    )
                }
            }
            .onFailure { e -> update { copy(loading = false, error = e.message ?: "Delete failed") } }
    }

    fun openAttachment(path: String) = viewModelScope.launch {
        if (!SupabaseProvider.enabled) return@launch
        runCatching { repo.signedUrl(path) }
            .onSuccess { url -> update { copy(openUrl = url) } }
            .onFailure { e -> update { copy(error = e.message) } }
    }

    fun clearOpenUrl() = update { copy(openUrl = null) }

    fun markNotificationRead(notification: Notification) = viewModelScope.launch {
        if (!SupabaseProvider.enabled || notification.readAt != null) return@launch
        runCatching { repo.markNotificationRead(notification.id) }
            .onSuccess { update { copy(notifications = notifications.map { if (it.id == notification.id) it.copy(readAt = "read") else it }) } }
    }

    fun back() = update {
        copy(
            screen = when (profile?.role) {
                "admin" -> ScreenState.AdminHome
                else -> ScreenState.UserHome
            },
            attachments = emptyList(),
            error = null
        )
    }

    fun logout() = viewModelScope.launch {
        if (SupabaseProvider.enabled) runCatching { repo.signOut() }
        _ui.value = UiState()
    }

    private fun updateIssueInMemory(issueId: String, status: String) {
        update {
            val updatedIssues = issues.map { if (it.id == issueId) it.copy(status = status) else it }
            val updatedScreen = when (val current = screen) {
                is ScreenState.AdminIssue -> if (current.issue.id == issueId) ScreenState.AdminIssue(current.issue.copy(status = status)) else current
                is ScreenState.UserIssue -> if (current.issue.id == issueId) ScreenState.UserIssue(current.issue.copy(status = status)) else current
                else -> current
            }
            copy(issues = updatedIssues, screen = updatedScreen)
        }
    }

    private fun update(block: UiState.() -> UiState) { _ui.value = _ui.value.block() }

    private fun demoIssues(): List<Issue> = listOf(
        Issue("demo-482", 482, "demo-user", "GTA IV crashes after entering gameplay", "Game launches successfully, then crashes after entering gameplay.", "Crash", "open", "GTA IV", driver = "Mali 26.2", emulator = "Winlator 10.1", dxvk = "2.4.1", wine = "9.2", gpu = "Mali-G57 MC2", soc = "Dimensity 6100+", androidVersion = "Android 15"),
        Issue("demo-481", 481, "demo-user2", "Black textures in Call of Duty", "Textures become black while the game remains responsive.", "Graphics", "investigating", "Call of Duty Black Ops", driver = "Mali 26.2", emulator = "GameHub", dxvk = "2.4.1", wine = "9.2", gpu = "Mali-G57 MC2", soc = "Helio G99", androidVersion = "Android 14"),
        Issue("demo-480", 480, "demo-user3", "Vulkan device lost during loading", "The emulator reports a Vulkan device loss during map loading.", "Vulkan Error", "testing", "Skyrim", driver = "Mali 26.2", emulator = "Winlator", dxvk = "1.10.3", wine = "9.2", gpu = "Mali-G57 MC2", androidVersion = "Android 15")
    )
}
