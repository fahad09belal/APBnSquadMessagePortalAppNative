/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.froyo.apbnsquad.portal.data.model.Message
import com.froyo.apbnsquad.portal.data.model.Profile
import com.froyo.apbnsquad.portal.data.model.UserStatus
import com.froyo.apbnsquad.portal.data.remote.SupabaseClient
import com.froyo.apbnsquad.portal.ui.components.MessageItem
import com.froyo.apbnsquad.portal.ui.components.MsnAvatar
import com.froyo.apbnsquad.portal.ui.components.XpButton
import com.froyo.apbnsquad.portal.ui.components.XpButtonStyle
import com.froyo.apbnsquad.portal.ui.components.TypingIndicatorView
import com.froyo.apbnsquad.portal.ui.components.XpTaskbar
import com.froyo.apbnsquad.portal.ui.components.XpWindow
import com.froyo.apbnsquad.portal.ui.theme.XpColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MessengerScreen(
    currentProfile: Profile?,
    currentStatus: UserStatus,
    messages: List<Message>,
    isLoadingMessages: Boolean,
    typingUsers: List<com.froyo.apbnsquad.portal.data.remote.TypingUser> = emptyList(),
    supabaseClient: SupabaseClient,
    onSendMessage: (content: String, replyToId: String?) -> Unit,
    onDeleteMessage: (Message) -> Unit,
    onTyping: (Boolean) -> Unit = {},
    onStatusClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAdminClick: () -> Unit,
    onOpenAboutUs: () -> Unit,
    onUserClick: (String) -> Unit,
    onImagePreview: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var sidebarOpen by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    var replyingToMessage by remember { mutableStateOf<Message?>(null) }
    var attachedUri by remember { mutableStateOf<Uri?>(null) }
    var isUploadingAttachment by remember { mutableStateOf(false) }
    var typingJob by remember { mutableStateOf<Job?>(null) }
    var isSearchOpen by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredMessages = remember(messages, searchQuery) {
        val q = searchQuery.trim()
        if (q.isEmpty()) {
            messages
        } else {
            messages.filter { msg ->
                msg.content.contains(q, ignoreCase = true) ||
                (msg.authorProfile?.effectiveDisplayName?.contains(q, ignoreCase = true) == true) ||
                (msg.authorProfile?.username?.contains(q, ignoreCase = true) == true)
            }
        }
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Auto-scroll to latest messages
    LaunchedEffect(filteredMessages.size, searchQuery) {
        if (filteredMessages.isNotEmpty() && searchQuery.isEmpty()) {
            listState.animateScrollToItem(filteredMessages.size - 1)
        }
    }

    BackHandler(enabled = sidebarOpen || isSearchOpen) {
        if (sidebarOpen) {
            sidebarOpen = false
        } else if (isSearchOpen) {
            isSearchOpen = false
            searchQuery = ""
        }
    }

    // Media picker launcher (Images, GIFs, Videos)
    val attachmentPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            attachedUri = uri
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color(0xFF041C3D))
    ) {
        // Desktop Wallpaper
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/background.png")
                .crossfade(false)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Main MSN Messenger Window Frame
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 42.dp)
        ) {
            XpWindow(
                title = "APBn Squad Message Portal",
                iconUrl = "file:///android_asset/group-logo.png",
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(XpColors.MsnWindowBg)
                ) {
                    // MSN Top Toolbar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .background(Color(0xFFE4EDF6))
                            .border(1.dp, Color(0xFFB5C4D4))
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Mobile Profile & Sidebar Toggle
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .clickable { sidebarOpen = !sidebarOpen },
                                contentAlignment = Alignment.Center
                            ) {
                                MsnAvatar(
                                    avatarUrl = currentProfile?.avatarUrl,
                                    status = currentStatus,
                                    size = 28.dp
                                )
                            }

                            XpButton(
                                onClick = onSettingsClick,
                                style = XpButtonStyle.SECONDARY,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text(
                                    text = "Settings",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B3D66)
                                )
                            }

                            if (currentProfile?.isAdmin == true) {
                                XpButton(
                                    onClick = onAdminClick,
                                    style = XpButtonStyle.SECONDARY,
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(24.dp)
                                ) {
                                    Text(
                                        text = "Admin",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B3D66)
                                    )
                                }
                            }

                            XpButton(
                                onClick = {
                                    isSearchOpen = !isSearchOpen
                                    if (!isSearchOpen) searchQuery = ""
                                },
                                style = if (isSearchOpen) XpButtonStyle.PRIMARY else XpButtonStyle.SECONDARY,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search messages",
                                        tint = Color(0xFF1B3D66),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (isSearchOpen) "Hide" else "Search",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B3D66)
                                    )
                                }
                            }
                        }

                        // Status Pill (Online, DND, Idle)
                        Row(
                            modifier = Modifier
                                .height(24.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFADC3DC), RoundedCornerShape(12.dp))
                                .clickable(onClick = onStatusClick)
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(currentStatus.colorHex))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = currentStatus.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF173A68)
                            )
                        }
                    }

                    // Windows XP Interactive Search & Filter Bar
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isSearchOpen,
                        enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
                        exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE9F2FB))
                                .border(1.dp, Color(0xFFBACDDF))
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color(0xFF144F8F),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))

                            // Sunken input field with robust vertical centering and high-contrast visible text
                            val searchBoxShape = RoundedCornerShape(3.dp)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                                    .clip(searchBoxShape)
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFF5A799B), searchBoxShape)
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        color = Color(0xFF10253F),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily.SansSerif
                                    ),
                                    cursorBrush = SolidColor(Color(0xFF144F8F)),
                                    decorationBox = { innerTextField ->
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            if (searchQuery.isEmpty()) {
                                                Text(
                                                    text = "Search messages by keyword…",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Normal,
                                                    color = Color(0xFF5A7088),
                                                    fontFamily = FontFamily.SansSerif
                                                )
                                            }
                                            innerTextField()
                                        }
                                    }
                                )
                            }

                            if (searchQuery.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear search query",
                                        tint = Color(0xFF7C0000),
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            // Match Count Badge
                            val matchCount = filteredMessages.size
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (searchQuery.isBlank()) Color(0xFFE2ECF7)
                                        else if (matchCount > 0) Color(0xFFD3E7FF)
                                        else Color(0xFFFFE0E0)
                                    )
                                    .border(
                                        1.dp,
                                        if (searchQuery.isBlank()) Color(0xFFADC4DC)
                                        else if (matchCount > 0) Color(0xFF8BB5E5)
                                        else Color(0xFFE5A1A1),
                                        RoundedCornerShape(3.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (searchQuery.isBlank()) "${messages.size} msgs" else "$matchCount match${if (matchCount == 1) "" else "es"}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (searchQuery.isBlank()) Color(0xFF334E6B) else if (matchCount > 0) Color(0xFF0D437F) else Color(0xFF990000)
                                )
                            }
                        }
                    }

                    // Workspace Area: Sidebar (overlay drawer) + Chat Main Area
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        // Main Chat Conversation Area
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Chat Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(XpColors.MsnChatHeaderBg)
                                    .border(1.dp, XpColors.MsnChatHeaderBorder)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "APBn Squad",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF173A68),
                                        fontFamily = FontFamily.SansSerif
                                    )
                                    Text(
                                        text = "Group message conversation · Real-time",
                                        fontSize = 10.sp,
                                        color = Color(0xFF566579),
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                            }

                            // Message Stream
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .background(Color(0xFFF3F8FD))
                            ) {
                                if (isLoadingMessages && messages.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            CircularProgressIndicator(
                                                color = Color(0xFF1769D1),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "Loading squad messages…",
                                                fontSize = 11.sp,
                                                color = Color(0xFF566579)
                                            )
                                        }
                                    }
                                } else if (messages.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No messages yet. Start the conversation.",
                                            fontSize = 12.sp,
                                            color = Color(0xFF566579)
                                        )
                                    }
                                } else if (filteredMessages.isEmpty() && searchQuery.isNotBlank()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = null,
                                                tint = Color(0xFF8899AA),
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = "No messages found matching \"$searchQuery\"",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1B3D66)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Try searching for a different word or username.",
                                                fontSize = 11.sp,
                                                color = Color(0xFF5C6F84)
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            XpButton(
                                                onClick = { searchQuery = "" },
                                                style = XpButtonStyle.SECONDARY
                                            ) {
                                                Text(
                                                    text = "Clear Search",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF144F8F)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        state = listState,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(filteredMessages, key = { it.id }) { message ->
                                            MessageItem(
                                                message = message,
                                                currentUserId = currentProfile?.id,
                                                isCurrentUserAdmin = currentProfile?.isAdmin == true,
                                                highlightQuery = searchQuery,
                                                onReplyClick = { target ->
                                                    replyingToMessage = target
                                                },
                                                onDeleteClick = onDeleteMessage,
                                                onImageClick = onImagePreview,
                                                onUserClick = onUserClick
                                            )
                                        }
                                    }
                                }
                            }

                            // Message Composer Area
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .imePadding()
                                    .background(Color(0xFFE3EDF7))
                                    .border(1.dp, Color(0xFFB5C4D4))
                            ) {
                                // Active Reply Banner
                                if (replyingToMessage != null) {
                                    val replyAuthor = replyingToMessage?.authorProfile?.effectiveDisplayName ?: "Squad Member"
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFFFF7CF))
                                            .border(1.dp, Color(0xFFDFCA79))
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Replying to $replyAuthor",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF655522)
                                        )
                                        IconButton(
                                            onClick = { replyingToMessage = null },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Cancel reply",
                                                tint = Color(0xFF655522),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                                // Attached Media Preview Chip (zero exposed links)
                                if (attachedUri != null) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFE2EDF8))
                                            .border(1.dp, Color(0xFFADC3DC))
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(context)
                                                    .data(attachedUri)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = "Attached thumbnail",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(30.dp)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .border(1.dp, Color(0xFF91A8BF), RoundedCornerShape(3.dp))
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "Media attached",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF144F8F)
                                                )
                                                Text(
                                                    text = "Ready to send",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF566579)
                                                )
                                            }
                                        }
                                        IconButton(
                                            onClick = { attachedUri = null },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove attachment",
                                                tint = Color(0xFF7C0000),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }

                                // Helper function to send message and background-upload media without showing links
                                val handleSend = {
                                    typingJob?.cancel()
                                    onTyping(false)
                                    val textToSend = inputText.trim()
                                    if (textToSend.isNotEmpty() || attachedUri != null) {
                                        if (attachedUri != null) {
                                            coroutineScope.launch {
                                                isUploadingAttachment = true
                                                try {
                                                    val bytes = context.contentResolver.openInputStream(attachedUri!!)?.use { it.readBytes() }
                                                    if (bytes != null) {
                                                        val result = supabaseClient.uploadImageToImgBB(bytes)
                                                        if (result.isSuccess) {
                                                            val uploadedUrl = result.getOrThrow()
                                                            val combined = if (textToSend.isNotEmpty()) "$textToSend $uploadedUrl" else uploadedUrl
                                                            onSendMessage(combined, replyingToMessage?.id)
                                                            inputText = ""
                                                            attachedUri = null
                                                            replyingToMessage = null
                                                        }
                                                    }
                                                } catch (_: Exception) {
                                                } finally {
                                                    isUploadingAttachment = false
                                                }
                                            }
                                        } else {
                                            onSendMessage(textToSend, replyingToMessage?.id)
                                            inputText = ""
                                            replyingToMessage = null
                                        }
                                    }
                                }

                                // Real-time MSN typing indicator
                                TypingIndicatorView(typingUsers = typingUsers)

                                // Input Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Image Attachment Button
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(Color.White)
                                            .border(1.dp, Color(0xFF8197AD), RoundedCornerShape(3.dp))
                                            .clickable(enabled = !isUploadingAttachment) {
                                                attachmentPicker.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                                )
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isUploadingAttachment) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp,
                                                color = Color(0xFF1769D1)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Image,
                                                contentDescription = "Attach image or video",
                                                tint = Color(0xFF175FBA),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Sunken Input Field
                                    val shape = RoundedCornerShape(2.dp)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(34.dp)
                                            .clip(shape)
                                            .background(Color.White)
                                            .border(1.dp, Color(0xFF8197AD), shape)
                                            .drawBehind {
                                                drawLine(
                                                    color = Color(0x33000000),
                                                    start = Offset(0f, 0f),
                                                    end = Offset(size.width, 0f),
                                                    strokeWidth = 1.5f
                                                )
                                            }
                                            .padding(horizontal = 8.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (inputText.isEmpty() && attachedUri == null) {
                                            Text(
                                                text = "Type a squad message…",
                                                fontSize = 11.sp,
                                                color = Color(0xFF8899AA)
                                            )
                                        } else if (inputText.isEmpty() && attachedUri != null) {
                                            Text(
                                                text = "Add a caption (optional)…",
                                                fontSize = 11.sp,
                                                color = Color(0xFF8899AA)
                                            )
                                        }
                                        BasicTextField(
                                            value = inputText,
                                            onValueChange = {
                                                if (it.length <= 2000) {
                                                    inputText = it
                                                    if (it.isNotBlank()) {
                                                        onTyping(true)
                                                        typingJob?.cancel()
                                                        typingJob = coroutineScope.launch {
                                                            delay(2500)
                                                            onTyping(false)
                                                        }
                                                    } else {
                                                        typingJob?.cancel()
                                                        onTyping(false)
                                                    }
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                            keyboardActions = KeyboardActions(
                                                onSend = { handleSend() }
                                            ),
                                            textStyle = TextStyle(
                                                color = Color(0xFF111111),
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.SansSerif
                                            ),
                                            cursorBrush = SolidColor(Color.Black)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Send Button
                                    XpButton(
                                        onClick = { handleSend() },
                                        enabled = (inputText.isNotBlank() || attachedUri != null) && !isUploadingAttachment
                                    ) {
                                        Text(
                                            text = if (isUploadingAttachment) "Sending…" else "Send",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF102D4B)
                                        )
                                    }
                                }
                            }
                        }

                        // Collapsible Profile & Squad Sidebar
                        androidx.compose.animation.AnimatedVisibility(
                            visible = sidebarOpen,
                            enter = slideInHorizontally { -it },
                            exit = slideOutHorizontally { -it },
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(250.dp)
                                .zIndex(10f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(XpColors.MsnSidebarBg)
                                    .border(1.dp, XpColors.MsnSidebarBorder)
                                    .padding(10.dp)
                            ) {
                                // Profile Box
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFFCCDDEE), RoundedCornerShape(4.dp))
                                        .clickable {
                                            currentProfile?.let { onUserClick(it.id) }
                                        }
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    MsnAvatar(
                                        avatarUrl = currentProfile?.avatarUrl,
                                        status = currentStatus,
                                        size = 44.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = currentProfile?.effectiveDisplayName ?: "User",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF173A68)
                                        )
                                        Text(
                                            text = "@${currentProfile?.username ?: "user"}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF5C6F84)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(Color(0xFFE7EEF5))
                                                .border(1.dp, Color(0xFFB5C4D4), RoundedCornerShape(2.dp))
                                                .clickable(onClick = onStatusClick)
                                                .padding(horizontal = 4.dp, vertical = 1.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = currentStatus.label,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF102D4B)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "▼",
                                                fontSize = 7.sp,
                                                color = Color(0xFF102D4B)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "SQUAD",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A607A)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Active Squad Contact
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(XpColors.MsnContactActive)
                                        .border(1.dp, Color(0xFF91B5DC), RoundedCornerShape(3.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF2AA84A))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "APBn Squad",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF173A68)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "PORTAL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A607A)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "● Group messages",
                                    fontSize = 11.sp,
                                    color = Color(0xFF23384D),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                                Text(
                                    text = "● Replies & mentions",
                                    fontSize = 11.sp,
                                    color = Color(0xFF23384D),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                // Portal Rules Warning Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(XpColors.MsnRulesBg)
                                        .border(1.dp, XpColors.MsnRulesBorder, RoundedCornerShape(3.dp))
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Portal rules",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = XpColors.MsnRulesText
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Keep messages appropriate. Administrators may remove messages or ban accounts for rule violations.",
                                            fontSize = 9.sp,
                                            color = XpColors.MsnRulesText,
                                            lineHeight = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Windows XP Taskbar
        XpTaskbar(
            onStartClick = onOpenAboutUs,
            onWindowTabClick = {},
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
