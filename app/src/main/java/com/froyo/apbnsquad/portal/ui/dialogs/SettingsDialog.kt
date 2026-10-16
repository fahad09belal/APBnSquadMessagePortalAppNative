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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.froyo.apbnsquad.portal.data.local.SessionManager
import com.froyo.apbnsquad.portal.service.NotificationHelper
import com.froyo.apbnsquad.portal.ui.components.XpButton
import com.froyo.apbnsquad.portal.ui.components.XpButtonStyle
import com.froyo.apbnsquad.portal.ui.components.XpWindow

@Composable
fun SettingsDialog(
    sessionManager: SessionManager,
    onSignOut: () -> Unit,
    onDismiss: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(sessionManager.notificationsEnabled) }
    var testNotificationSent by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        XpWindow(
            title = "Settings",
            iconUrl = "file:///android_asset/group-logo.png",
            onClose = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Settings",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF173A68),
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "Manage portal preferences and account security.",
                    fontSize = 11.sp,
                    color = Color(0xFF5D6D7E),
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Notifications Section
                val sectionShape = RoundedCornerShape(4.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(sectionShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFC7D7E8), sectionShape)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Notifications",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF173A68)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Desktop / Mobile Notifications",
                                fontSize = 11.sp,
                                color = Color(0xFF222222)
                            )
                            Checkbox(
                                checked = notificationsEnabled,
                                onCheckedChange = {
                                    notificationsEnabled = it
                                    sessionManager.notificationsEnabled = it
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF1769D1),
                                    checkmarkColor = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        XpButton(
                            onClick = {
                                NotificationHelper.showMessageNotification(
                                    context = context,
                                    title = "APBn Squad Portal",
                                    body = "This is a test notification from the APBn Squad Portal."
                                )
                                testNotificationSent = true
                            },
                            style = XpButtonStyle.SECONDARY
                        ) {
                            Text(
                                text = if (testNotificationSent) "Notification Sent!" else "Send Test Notification",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF102D4B)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Receive alerts when new squad messages arrive.",
                            fontSize = 10.sp,
                            color = Color(0xFF5C6F84)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Account Actions Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(sectionShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFC7D7E8), sectionShape)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Account Actions",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF173A68)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        XpButton(
                            onClick = {
                                onSignOut()
                                onDismiss()
                            },
                            style = XpButtonStyle.SECONDARY,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Sign Out",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF21384F)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        XpButton(
                            onClick = {
                                onSignOut()
                                onDismiss()
                            },
                            style = XpButtonStyle.DANGER,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Delete Account",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7C0000)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Package: ${com.froyo.apbnsquad.portal.BuildConfig.APPLICATION_ID} • v${com.froyo.apbnsquad.portal.BuildConfig.VERSION_NAME}",
                    fontSize = 10.sp,
                    color = Color(0xFF5D6D7E),
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                XpButton(
                    onClick = onDismiss,
                    style = XpButtonStyle.PRIMARY,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Close",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF102D4B)
                    )
                }
            }
        }
    }
}
