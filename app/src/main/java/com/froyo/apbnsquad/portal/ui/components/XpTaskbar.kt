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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.froyo.apbnsquad.portal.ui.theme.XpColors
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun XpTaskbar(
    onStartClick: () -> Unit,
    onWindowTabClick: () -> Unit,
    modifier: Modifier = Modifier,
    taskTitle: String = "APBn Squad Portal",
    taskIconUrl: String = "file:///android_asset/group-logo.png"
) {
    var currentTimeString by remember { mutableStateOf("") }
    var currentDateString by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("M/d/yyyy", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTimeString = timeFormat.format(now)
            currentDateString = dateFormat.format(now)
            delay(1000)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(XpColors.TaskbarBrush)
            .border(
                width = 1.dp,
                color = XpColors.TaskbarTopBorder,
                shape = RoundedCornerShape(0.dp)
            )
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Start Orb + Active App Taskbar Item
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Glowing Windows Start Orb
                val context = LocalContext.current
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF175FBA))
                        .border(1.5.dp, XpColors.StartOrbBorder, CircleShape)
                        .clickable(onClick = onStartClick),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("file:///android_asset/custom-logo.png")
                            .crossfade(false)
                            .build(),
                        contentDescription = "Start",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Running App Taskbar Item
                val itemShape = RoundedCornerShape(3.dp)
                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .clip(itemShape)
                        .background(XpColors.TaskbarItemActiveBg)
                        .border(1.dp, XpColors.TaskbarItemBorder, itemShape)
                        .clickable(onClick = onWindowTabClick)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(taskIconUrl)
                                .crossfade(false)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = taskTitle,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.SansSerif,
                            maxLines = 1
                        )
                    }
                }
            }

            // Right: Digital Clock & Date
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(
                        text = currentTimeString,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        lineHeight = 12.sp,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = currentDateString,
                        color = Color(0xFFD6E7F8),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.SansSerif,
                        lineHeight = 10.sp,
                        textAlign = TextAlign.End
                    )
                }

                // Show Desktop Bar at edge
                Box(
                    modifier = Modifier
                        .size(width = 9.dp, height = 30.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(1.dp))
                )
            }
        }
    }
}
