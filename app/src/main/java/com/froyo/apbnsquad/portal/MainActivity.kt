/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 * Portfolio: https://fahadnway.qd.je
 */

package com.froyo.apbnsquad.portal

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.froyo.apbnsquad.portal.ui.PortalViewModel
import com.froyo.apbnsquad.portal.ui.dialogs.AboutUsDialog
import com.froyo.apbnsquad.portal.ui.dialogs.AdminDialog
import com.froyo.apbnsquad.portal.ui.dialogs.EditProfileDialog
import com.froyo.apbnsquad.portal.ui.dialogs.ImageViewerDialog
import com.froyo.apbnsquad.portal.ui.dialogs.ProfilePopoverDialog
import com.froyo.apbnsquad.portal.ui.dialogs.SettingsDialog
import com.froyo.apbnsquad.portal.ui.dialogs.StatusMenuDialog
import com.froyo.apbnsquad.portal.ui.screens.AuthScreen
import com.froyo.apbnsquad.portal.ui.screens.MessengerScreen
import com.froyo.apbnsquad.portal.ui.theme.MyApplicationTheme

/**
 * Genuine Native Android Application for APBn Squad Message Portal.
 * Recreates the MSN Messenger and Windows XP UI using 100% Jetpack Compose.
 * No WebViews used.
 */
class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            // Notification permission result handled
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request POST_NOTIFICATIONS on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF041C3D)
                ) {
                    PortalNativeApp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val sessionManager = com.froyo.apbnsquad.portal.data.local.SessionManager(this)
        if (sessionManager.isLoggedIn) {
            com.froyo.apbnsquad.portal.service.PortalBackgroundService.start(this)
        }
    }
}

@Composable
fun PortalNativeApp(
    viewModel: PortalViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (!uiState.isLoggedIn) {
        AuthScreen(
            onSignIn = { username, password, onError ->
                viewModel.signIn(username, password, onError)
            },
            onSignUp = { username, password, onSuccess, onError ->
                viewModel.signUp(username, password, onSuccess, onError)
            },
            onOpenAboutUs = {
                viewModel.showAboutUs(true)
            }
        )
    } else {
        val typingUsers by viewModel.typingUsers.collectAsStateWithLifecycle()
        MessengerScreen(
            currentProfile = uiState.currentProfile,
            currentStatus = uiState.currentStatus,
            messages = uiState.messages,
            isLoadingMessages = uiState.isLoadingMessages,
            typingUsers = typingUsers,
            supabaseClient = viewModel.supabaseClient,
            onSendMessage = { content, replyToId ->
                viewModel.sendMessage(content, replyToId)
            },
            onDeleteMessage = { message ->
                viewModel.deleteMessage(message)
            },
            onTyping = { isTyping ->
                viewModel.sendTyping(isTyping)
            },
            onStatusClick = {
                viewModel.showStatusMenu(true)
            },
            onSettingsClick = {
                viewModel.showSettings(true)
            },
            onAdminClick = {
                viewModel.showAdmin(true)
            },
            onOpenAboutUs = {
                viewModel.showAboutUs(true)
            },
            onUserClick = { userId ->
                viewModel.openUserPopoverById(userId)
            },
            onImagePreview = { url ->
                viewModel.showImagePreview(url)
            }
        )
    }

    // Modal Dialogs
    if (uiState.showAboutUsDialog) {
        AboutUsDialog(
            onDismiss = { viewModel.showAboutUs(false) }
        )
    }

    if (uiState.showSettingsDialog) {
        SettingsDialog(
            sessionManager = viewModel.sessionManager,
            onSignOut = {
                viewModel.signOut()
            },
            onDismiss = { viewModel.showSettings(false) }
        )
    }

    if (uiState.showAdminDialog) {
        val token = viewModel.sessionManager.accessToken.orEmpty()
        val userId = viewModel.sessionManager.userId.orEmpty()
        AdminDialog(
            currentUserId = userId,
            token = token,
            supabaseClient = viewModel.supabaseClient,
            onDismiss = { viewModel.showAdmin(false) }
        )
    }

    if (uiState.showEditProfileDialog) {
        EditProfileDialog(
            currentProfile = uiState.currentProfile,
            supabaseClient = viewModel.supabaseClient,
            onSave = { displayName, avatarUrl, bio ->
                viewModel.updateProfile(displayName, avatarUrl, bio)
            },
            onDismiss = { viewModel.showEditProfile(false) }
        )
    }

    if (uiState.showStatusMenuDialog) {
        StatusMenuDialog(
            currentStatus = uiState.currentStatus,
            onStatusSelected = { newStatus ->
                viewModel.setStatus(newStatus)
            },
            onDismiss = { viewModel.showStatusMenu(false) }
        )
    }

    val popover = uiState.popoverProfile
    if (popover != null) {
        ProfilePopoverDialog(
            profile = popover,
            isSelf = popover.id == viewModel.sessionManager.userId,
            onEditProfile = {
                viewModel.showEditProfile(true)
            },
            onDismiss = { viewModel.showUserPopover(null) }
        )
    }

    val previewImage = uiState.previewImageUrl
    if (!previewImage.isNullOrBlank()) {
        ImageViewerDialog(
            imageUrl = previewImage,
            onDismiss = { viewModel.showImagePreview(null) }
        )
    }
}
