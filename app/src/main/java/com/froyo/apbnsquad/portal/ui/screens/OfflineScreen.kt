/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.froyo.apbnsquad.portal.ui.components.XpButton
import com.froyo.apbnsquad.portal.ui.components.XpTaskbar
import com.froyo.apbnsquad.portal.ui.components.XpWindow
import com.froyo.apbnsquad.portal.ui.theme.XpColors
import kotlinx.coroutines.delay

@Composable
fun OfflineScreen(
    onTryAgain: () -> Unit,
    onOpenAboutUs: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isChecking by remember { mutableStateOf(false) }
    var hintText by remember { mutableStateOf("Check your internet connection and try again.") }
    val context = LocalContext.current

    LaunchedEffect(isChecking) {
        if (isChecking) {
            hintText = "Trying to reconnect…"
            delay(1500)
            onTryAgain()
            isChecking = false
            hintText = "Check your internet connection and try again."
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF041C3D))
    ) {
        // Windows XP Desktop Wallpaper
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/background.png")
                .crossfade(false)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Desktop Icons on the left (matching offline.html)
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onOpenAboutUs)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("file:///android_asset/custom-logo.png")
                        .crossfade(false)
                        .build(),
                    contentDescription = "About us",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(42.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "about_us.exe",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(1f, 1f),
                            blurRadius = 3f
                        )
                    )
                )
            }
        }

        // Centered offline.exe Window
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            XpWindow(
                title = "offline.exe",
                iconUrl = "file:///android_asset/custom-logo.png",
                onClose = { isChecking = true },
                modifier = Modifier.fillMaxWidth(0.92f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Beveled Offline Icon Badge
                    val iconShape = RoundedCornerShape(16.dp)
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .shadow(4.dp, iconShape)
                            .clip(iconShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.White, Color(0xFFD8E8F8))
                                )
                            )
                            .border(1.dp, Color(0xFF9DB4CC), iconShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = "Offline",
                            tint = Color(0xFF1B4775),
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Look like you're offline",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF173A68),
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Please refresh or close the app and open it again.",
                        fontSize = 13.sp,
                        color = Color(0xFF34485E),
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Try Again Button
                    XpButton(
                        onClick = { isChecking = true },
                        enabled = !isChecking,
                        modifier = Modifier.width(120.dp)
                    ) {
                        if (isChecking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF102D4B)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = if (isChecking) "Checking…" else "Try Again",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF102D4B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = hintText,
                        fontSize = 10.sp,
                        color = Color(0xFF718093),
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Bottom Taskbar with offline.exe Running Task Tab
        XpTaskbar(
            onStartClick = onOpenAboutUs,
            onWindowTabClick = { isChecking = true },
            taskTitle = "offline.exe",
            taskIconUrl = "file:///android_asset/custom-logo.png",
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
