/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.froyo.apbnsquad.portal.data.model.Message
import com.froyo.apbnsquad.portal.data.model.UserStatus
import com.froyo.apbnsquad.portal.ui.theme.XpColors

@Composable
fun MessageItem(
    message: Message,
    currentUserId: String?,
    isCurrentUserAdmin: Boolean,
    onReplyClick: (Message) -> Unit,
    onDeleteClick: (Message) -> Unit,
    onImageClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    highlightQuery: String = "",
    modifier: Modifier = Modifier
) {
    val isMine = message.userId == currentUserId
    val isDeleted = message.deleted
    val author = message.authorProfile
    val displayName = author?.displayName?.takeIf { it.isNotBlank() } ?: author?.username ?: "Squad Member"
    val username = author?.username ?: "user"
    val avatarUrl = author?.avatarUrl

    val bg = if (isMine) XpColors.MessageMineBg else XpColors.MessageOtherBg
    val shape = RoundedCornerShape(6.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(shape)
            .background(bg)
            .border(1.dp, XpColors.MessageBorder, shape)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            // MSN Messenger Style User Avatar
            MsnAvatar(
                avatarUrl = avatarUrl,
                status = UserStatus.ONLINE,
                size = 36.dp,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .clickable { onUserClick(message.userId) }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Header: Author Display Name, @username, Admin Badge, Timestamp
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        HighlightedText(
                            text = displayName,
                            highlight = highlightQuery,
                            fontSize = 12.sp,
                            color = Color(0xFF173A68),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onUserClick(message.userId) }
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        HighlightedText(
                            text = "@$username",
                            highlight = highlightQuery,
                            fontSize = 10.sp,
                            color = Color(0xFF5C6F84)
                        )

                        if (author?.isAdmin == true) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFFFFE082))
                                    .border(1.dp, Color(0xFFDFCA79), RoundedCornerShape(3.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "ADMIN",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4A2800)
                                )
                            }
                        }
                    }

                    Text(
                        text = message.formattedTime,
                        fontSize = 10.sp,
                        color = Color(0xFF7B8793),
                        fontFamily = FontFamily.SansSerif
                    )
                }

                // Reply Quote Banner if present
                val replyTarget = message.replyTargetMessage
                if (replyTarget != null) {
                    val replyAuthor = replyTarget.authorProfile?.effectiveDisplayName ?: "Squad Member"
                    val replySnippet = if (replyTarget.deleted) "[deleted]" else replyTarget.content
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(3.dp))
                            .background(XpColors.MessageReplyQuoteBg)
                            .border(1.dp, Color(0xFFB5C4D4), RoundedCornerShape(3.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(width = 3.dp, height = 22.dp)
                                    .background(XpColors.MessageReplyQuoteBorder)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Replying to $replyAuthor",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF144F8F)
                                )
                                Text(
                                    text = replySnippet,
                                    fontSize = 10.sp,
                                    color = Color(0xFF334455),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Content or Media
                if (isDeleted) {
                    Text(
                        text = message.content,
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFF8A0000),
                        fontFamily = FontFamily.SansSerif
                    )
                } else {
                    // Extract media URL and clean text so raw links are never shown
                    val (cleanedText, mediaUrl) = parseMediaFromContent(message.content)

                    if (cleanedText.isNotEmpty()) {
                        HighlightedText(
                            text = cleanedText,
                            highlight = highlightQuery,
                            fontSize = 12.sp,
                            color = XpColors.InputText
                        )
                    }

                    if (mediaUrl != null) {
                        if (cleanedText.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        val isVideo = mediaUrl.endsWith(".mp4", ignoreCase = true)
                        val context = LocalContext.current

                        if (isVideo) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.Black)
                                    .border(1.dp, Color(0xFF526B88), RoundedCornerShape(4.dp))
                                    .clickable { onImageClick(mediaUrl) }
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "▶ Play Video",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(mediaUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Attached media",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 220.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .border(1.dp, Color(0xFFADC3DC), RoundedCornerShape(4.dp))
                                    .clickable { onImageClick(mediaUrl) }
                            )
                        }
                    }
                }

                // Message Actions: Reply, Delete
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isDeleted) {
                        Text(
                            text = "Reply",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF064FA9),
                            modifier = Modifier
                                .clickable { onReplyClick(message) }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    if (!isDeleted && (isMine || isCurrentUserAdmin)) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isCurrentUserAdmin && !isMine) "Moderate" else "Delete",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC60000),
                            modifier = Modifier
                                .clickable { onDeleteClick(message) }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HighlightedText(
    text: String,
    highlight: String,
    fontSize: TextUnit,
    color: Color,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) {
    val trimmedHighlight = highlight.trim()
    if (trimmedHighlight.isEmpty()) {
        Text(
            text = text,
            fontSize = fontSize,
            color = color,
            fontWeight = fontWeight,
            fontFamily = FontFamily.SansSerif,
            lineHeight = 16.sp,
            modifier = modifier
        )
        return
    }

    val annotatedString = buildAnnotatedString {
        var startIndex = 0
        val lowerText = text.lowercase()
        val lowerHighlight = trimmedHighlight.lowercase()

        while (startIndex < text.length) {
            val index = lowerText.indexOf(lowerHighlight, startIndex)
            if (index == -1) {
                append(text.substring(startIndex))
                break
            }
            if (index > startIndex) {
                append(text.substring(startIndex, index))
            }
            withStyle(
                style = SpanStyle(
                    background = Color(0xFFFFEB3B), // Classic yellow highlighter
                    color = Color(0xFF111111),
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(text.substring(index, index + trimmedHighlight.length))
            }
            startIndex = index + trimmedHighlight.length
        }
    }

    Text(
        text = annotatedString,
        fontSize = fontSize,
        color = color,
        fontWeight = fontWeight,
        fontFamily = FontFamily.SansSerif,
        lineHeight = 16.sp,
        modifier = modifier
    )
}

private fun parseMediaFromContent(content: String): Pair<String, String?> {
    val regex = Regex("(https?://\\S+\\.(?:png|jpe?g|gif|webp|mp4)(?:\\?\\S*)?)|(https?://i\\.ibb\\.co/\\S+)", RegexOption.IGNORE_CASE)
    val match = regex.find(content)
    if (match != null) {
        val mediaUrl = match.value
        val cleanedText = content.replace(mediaUrl, "").trim()
        return Pair(cleanedText, mediaUrl)
    }
    return Pair(content, null)
}
