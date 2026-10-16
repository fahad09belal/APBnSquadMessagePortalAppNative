/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.froyo.apbnsquad.portal.data.model.UserStatus

@Composable
fun MsnAvatar(
    avatarUrl: String?,
    status: UserStatus? = null,
    size: Dp = 40.dp,
    showStatusBadge: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val effectiveUrl = if (avatarUrl.isNullOrBlank()) {
        "file:///android_asset/group-logo.png"
    } else {
        avatarUrl
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Main Avatar Image
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(effectiveUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.5.dp, Color(0xFF91A8BF), CircleShape)
        )

        // Status Badge Dot / Icon at bottom right
        if (showStatusBadge && status != null) {
            val badgeSize = (size.value * 0.35f).coerceIn(12f, 18f).dp
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
                    .size(badgeSize)
                    .clip(CircleShape)
                    .background(Color(status.colorHex))
                    .border(1.dp, Color.White, CircleShape)
            )
        }
    }
}
