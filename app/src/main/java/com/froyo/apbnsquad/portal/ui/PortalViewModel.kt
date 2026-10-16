/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.froyo.apbnsquad.portal.data.local.SessionManager
import com.froyo.apbnsquad.portal.data.model.Message
import com.froyo.apbnsquad.portal.data.model.Profile
import com.froyo.apbnsquad.portal.data.model.UserStatus
import com.froyo.apbnsquad.portal.data.remote.RealtimeTypingManager
import com.froyo.apbnsquad.portal.data.remote.SupabaseClient
import com.froyo.apbnsquad.portal.data.remote.TypingUser
import com.froyo.apbnsquad.portal.service.NotificationHelper
import com.froyo.apbnsquad.portal.service.PortalBackgroundService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PortalUiState(
    val isLoggedIn: Boolean = false,
    val currentProfile: Profile? = null,
    val currentStatus: UserStatus = UserStatus.ONLINE,
    val messages: List<Message> = emptyList(),
    val isLoadingMessages: Boolean = false,
    val showAboutUsDialog: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val showAdminDialog: Boolean = false,
    val showEditProfileDialog: Boolean = false,
    val showStatusMenuDialog: Boolean = false,
    val popoverProfile: Profile? = null,
    val previewImageUrl: String? = null
)

class PortalViewModel(application: Application) : AndroidViewModel(application) {
    val sessionManager = SessionManager(application)
    val supabaseClient = SupabaseClient()
    private val realtimeTypingManager = RealtimeTypingManager()

    val typingUsers: StateFlow<List<TypingUser>> = realtimeTypingManager.typingUsers
        .map { it.values.toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(
        PortalUiState(
            isLoggedIn = sessionManager.isLoggedIn,
            currentProfile = sessionManager.getCachedProfile(),
            currentStatus = sessionManager.status
        )
    )
    val uiState: StateFlow<PortalUiState> = _uiState.asStateFlow()

    private var messagePollingJob: Job? = null
    private var lastKnownMessageId: String? = null

    init {
        if (sessionManager.isLoggedIn) {
            refreshProfile()
            startMessagePolling()
            sessionManager.userId?.let { realtimeTypingManager.start(it) }
            PortalBackgroundService.start(application)
        }
    }

    fun signIn(username: String, password: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = supabaseClient.signIn(username, password)
            if (result.isSuccess) {
                val (token, userId) = result.getOrThrow()
                sessionManager.saveSession(token, userId, username)
                _uiState.update { it.copy(isLoggedIn = true) }
                refreshProfile()
                startMessagePolling()
                realtimeTypingManager.start(userId)
                PortalBackgroundService.start(getApplication())
            } else {
                val err = result.exceptionOrNull()?.message ?: "Login failed. Check your credentials."
                onError(err)
            }
        }
    }

    fun signUp(
        username: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            // First check if username already taken
            val exists = supabaseClient.checkUsernameExists(username).getOrDefault(false)
            if (exists) {
                onError("That username already exists.")
                return@launch
            }

            val result = supabaseClient.signUp(username, password)
            if (result.isSuccess) {
                val (token, userId) = result.getOrThrow()
                if (token.isNotBlank() && userId.isNotBlank()) {
                    sessionManager.saveSession(token, userId, username)
                    _uiState.update { it.copy(isLoggedIn = true) }
                    refreshProfile()
                    startMessagePolling()
                    realtimeTypingManager.start(userId)
                    PortalBackgroundService.start(getApplication())
                    onSuccess("Account created successfully!")
                } else {
                    onSuccess("Account created. You can now sign in.")
                }
            } else {
                val err = result.exceptionOrNull()?.message ?: "Sign up failed."
                onError(err)
            }
        }
    }

    fun signOut() {
        messagePollingJob?.cancel()
        realtimeTypingManager.stop()
        PortalBackgroundService.stop(getApplication())
        sessionManager.clearSession()
        _uiState.update {
            PortalUiState(
                isLoggedIn = false,
                currentProfile = null,
                messages = emptyList()
            )
        }
    }

    fun refreshProfile() {
        val uId = sessionManager.userId ?: return
        val token = sessionManager.accessToken ?: return
        viewModelScope.launch {
            val result = supabaseClient.fetchProfile(uId, token)
            if (result.isSuccess) {
                val profile = result.getOrThrow()
                if (profile.banned) {
                    signOut()
                    return@launch
                }
                sessionManager.saveProfile(profile)
                _uiState.update { it.copy(currentProfile = profile) }
            }
        }
    }

    fun startMessagePolling() {
        messagePollingJob?.cancel()
        messagePollingJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMessages = true) }
            while (isActive) {
                val token = sessionManager.accessToken
                if (!token.isNullOrBlank()) {
                    val result = supabaseClient.fetchMessages(token)
                    if (result.isSuccess) {
                        val fetchedMessages = result.getOrThrow()
                        val previousLatestId = lastKnownMessageId ?: sessionManager.lastKnownMessageId
                        val newLatestId = fetchedMessages.lastOrNull()?.id

                        // Check for new incoming messages from other squad members
                        if (previousLatestId != null && newLatestId != previousLatestId) {
                            val newMsgs = fetchedMessages.takeLastWhile { it.id != previousLatestId }
                            val currentUserId = sessionManager.userId
                            for (msg in newMsgs) {
                                if (msg.userId != currentUserId && !msg.deleted && sessionManager.notificationsEnabled) {
                                    val senderName = msg.authorProfile?.effectiveDisplayName ?: "Squad Member"
                                    NotificationHelper.showMessageNotification(
                                        context = getApplication(),
                                        title = senderName,
                                        body = msg.content
                                    )
                                }
                            }
                        }

                        lastKnownMessageId = newLatestId
                        if (newLatestId != null) {
                            sessionManager.lastKnownMessageId = newLatestId
                        }

                        _uiState.update {
                            it.copy(
                                messages = fetchedMessages,
                                isLoadingMessages = false
                            )
                        }
                    }
                }
                delay(3000)
            }
        }
    }

    fun sendMessage(content: String, replyToId: String?) {
        val uId = sessionManager.userId ?: return
        val token = sessionManager.accessToken ?: return
        viewModelScope.launch {
            sendTyping(false)
            supabaseClient.sendMessage(uId, content, replyToId, token)
            // Trigger instant refresh
            val result = supabaseClient.fetchMessages(token)
            if (result.isSuccess) {
                val msgs = result.getOrThrow()
                val latest = msgs.lastOrNull()?.id
                if (latest != null) {
                    lastKnownMessageId = latest
                    sessionManager.lastKnownMessageId = latest
                }
                _uiState.update { it.copy(messages = msgs) }
            }
        }
    }

    fun sendTyping(isTyping: Boolean) {
        val userId = sessionManager.userId ?: return
        val profile = _uiState.value.currentProfile ?: sessionManager.getCachedProfile()
        val username = profile?.username ?: sessionManager.username ?: "user"
        val displayName = profile?.effectiveDisplayName ?: username
        realtimeTypingManager.sendTyping(userId, username, displayName, isTyping)
    }

    fun deleteMessage(message: Message) {
        val uId = sessionManager.userId ?: return
        val token = sessionManager.accessToken ?: return
        val isAdmin = _uiState.value.currentProfile?.isAdmin == true
        viewModelScope.launch {
            supabaseClient.softDeleteMessage(message.id, uId, isAdmin, token)
            val result = supabaseClient.fetchMessages(token)
            if (result.isSuccess) {
                _uiState.update { it.copy(messages = result.getOrThrow()) }
            }
        }
    }

    fun setStatus(status: UserStatus) {
        sessionManager.status = status
        _uiState.update { it.copy(currentStatus = status) }
    }

    fun updateProfile(displayName: String?, avatarUrl: String?, bio: String?) {
        val uId = sessionManager.userId ?: return
        val token = sessionManager.accessToken ?: return
        viewModelScope.launch {
            supabaseClient.updateProfile(uId, displayName, avatarUrl, bio, token)
            refreshProfile()
        }
    }

    fun showAboutUs(show: Boolean) = _uiState.update { it.copy(showAboutUsDialog = show) }
    fun showSettings(show: Boolean) = _uiState.update { it.copy(showSettingsDialog = show) }
    fun showAdmin(show: Boolean) = _uiState.update { it.copy(showAdminDialog = show) }
    fun showEditProfile(show: Boolean) = _uiState.update { it.copy(showEditProfileDialog = show) }
    fun showStatusMenu(show: Boolean) = _uiState.update { it.copy(showStatusMenuDialog = show) }
    fun showUserPopover(profile: Profile?) = _uiState.update { it.copy(popoverProfile = profile) }
    fun showImagePreview(url: String?) = _uiState.update { it.copy(previewImageUrl = url) }

    fun openUserPopoverById(userId: String) {
        viewModelScope.launch {
            val token = sessionManager.accessToken ?: return@launch
            val result = supabaseClient.fetchProfile(userId, token)
            if (result.isSuccess) {
                showUserPopover(result.getOrThrow())
            }
        }
    }

    override fun onCleared() {
        realtimeTypingManager.stop()
        super.onCleared()
    }
}
