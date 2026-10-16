/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.froyo.apbnsquad.portal.ui.components.XpButton
import com.froyo.apbnsquad.portal.ui.components.XpWindow

@Composable
fun AboutUsDialog(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        XpWindow(
            title = "about_us.exe",
            iconUrl = "file:///android_asset/custom-logo.png",
            onClose = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F0F0))
                    .padding(16.dp)
            ) {
                // Header Banner
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "APBn Squad Message Portal",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003399),
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "Version ${com.froyo.apbnsquad.portal.BuildConfig.VERSION_NAME}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333),
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "Package: ${com.froyo.apbnsquad.portal.BuildConfig.APPLICATION_ID}",
                        fontSize = 10.sp,
                        color = Color(0xFF1769D1),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Copyright © APBn Squad. All rights reserved.",
                        fontSize = 10.sp,
                        color = Color(0xFF666666),
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFD0D0D0))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Developers & Contributors:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D426C),
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Developer Cards (Prantik & Fahad)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DevCard(
                        name = "Saimon Islam Prantik",
                        role = "Frontend + UI/UX",
                        avatarUrl = "file:///android_asset/dev_avatar_1.png",
                        modifier = Modifier.weight(1f)
                    )
                    DevCard(
                        name = "Fahad Al-Belal",
                        role = "Backend + Frontend",
                        avatarUrl = "file:///android_asset/dev_avatar_2.jpg",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFD0D0D0))
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Footer with OK button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    XpButton(
                        onClick = onDismiss,
                        modifier = Modifier.width(85.dp)
                    ) {
                        Text(
                            text = "OK",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF102D4B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DevCard(
    name: String,
    role: String,
    avatarUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val cardShape = RoundedCornerShape(6.dp)

    Column(
        modifier = modifier
            .clip(cardShape)
            .background(Color(0xFFF9FCFF))
            .border(1.dp, Color(0xFFB5C4D4), cardShape)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(avatarUrl)
                .crossfade(true)
                .build(),
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .border(2.dp, Color(0xFF7D96AF), CircleShape)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF112A46),
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif
        )

        Text(
            text = role,
            fontSize = 10.sp,
            color = Color(0xFF4A607A),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif
        )
    }
}
