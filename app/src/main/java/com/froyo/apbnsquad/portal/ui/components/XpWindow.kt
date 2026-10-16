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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.froyo.apbnsquad.portal.ui.theme.XpColors

@Composable
fun XpWindow(
    title: String,
    modifier: Modifier = Modifier,
    iconUrl: String = "file:///android_asset/group-logo.png",
    onMinimize: (() -> Unit)? = null,
    onMaximize: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val windowShape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .shadow(16.dp, windowShape)
            .clip(windowShape)
            .background(XpColors.CardBrush)
            .border(1.dp, XpColors.WindowBorder, windowShape)
            .border(2.dp, XpColors.WindowBorderOuter.copy(alpha = 0.5f), windowShape)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Windows XP Signature Glossy Titlebar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(XpColors.TitleBarBrush)
                    .padding(horizontal = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    val context = LocalContext.current
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(iconUrl)
                            .crossfade(false)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(19.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.White.copy(alpha = 0.85f))
                            .border(1.dp, Color(0xFFADC3DC), RoundedCornerShape(3.dp))
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        maxLines = 1
                    )
                }

                // Window Control Buttons (Minimize, Maximize, Close)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    XpWindowButton(
                        symbol = "_",
                        onClick = onMinimize ?: {},
                        isClose = false
                    )
                    XpWindowButton(
                        symbol = "□",
                        onClick = onMaximize ?: {},
                        isClose = false
                    )
                    XpWindowButton(
                        symbol = "×",
                        onClick = onClose ?: {},
                        isClose = true
                    )
                }
            }

            // Window Body Content
            content()
        }
    }
}

@Composable
fun XpWindowButton(
    symbol: String,
    onClick: () -> Unit,
    isClose: Boolean
) {
    val buttonShape = RoundedCornerShape(3.dp)
    val brush = if (isClose) XpColors.CloseButtonBrush else XpColors.ButtonBrush
    val borderColor = if (isClose) XpColors.CloseButtonBorder else Color(0xFFBCD5F3)

    Box(
        modifier = Modifier
            .size(width = 20.dp, height = 19.dp)
            .clip(buttonShape)
            .background(brush)
            .border(1.dp, borderColor, buttonShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = if (isClose) Color.White else Color(0xFF102D4B),
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 11.sp
        )
    }
}
