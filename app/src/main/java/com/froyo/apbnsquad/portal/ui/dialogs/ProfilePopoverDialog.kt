/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.froyo.apbnsquad.portal.data.model.Profile
import com.froyo.apbnsquad.portal.data.model.UserStatus
import com.froyo.apbnsquad.portal.ui.components.MsnAvatar
import com.froyo.apbnsquad.portal.ui.components.XpButton
import com.froyo.apbnsquad.portal.ui.components.XpButtonStyle

@Composable
fun ProfilePopoverDialog(
    profile: Profile,
    isSelf: Boolean,
    onEditProfile: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        val shape = RoundedCornerShape(8.dp)
        Box(
            modifier = Modifier
                .width(300.dp)
                .shadow(16.dp, shape)
                .clip(shape)
                .background(Color(0xFFF9FCFF))
                .border(1.dp, Color(0xFF526B88), shape)
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MsnAvatar(
                        avatarUrl = profile.avatarUrl,
                        status = UserStatus.ONLINE,
                        size = 54.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = profile.effectiveDisplayName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF173A68),
                            fontFamily = FontFamily.SansSerif
                        )
                        Text(
                            text = "@${profile.username}",
                            fontSize = 11.sp,
                            color = Color(0xFF5C6F84),
                            fontFamily = FontFamily.SansSerif
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (profile.isAdmin) Color(0xFFFFE082) else Color(0xFFE0E0E0))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = profile.role.uppercase(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (profile.isAdmin) Color(0xFF4A2800) else Color(0xFF333333)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFCCDDEE), RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = profile.bio?.ifEmpty { "No bio added yet." } ?: "No bio added yet.",
                        fontSize = 11.sp,
                        color = Color(0xFF334455),
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isSelf) {
                    XpButton(
                        onClick = {
                            onDismiss()
                            onEditProfile()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Edit Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                XpButton(
                    onClick = onDismiss,
                    style = XpButtonStyle.SECONDARY,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
