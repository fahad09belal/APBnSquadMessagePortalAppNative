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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.froyo.apbnsquad.portal.data.model.Profile
import com.froyo.apbnsquad.portal.data.remote.SupabaseClient
import com.froyo.apbnsquad.portal.ui.components.XpButton
import com.froyo.apbnsquad.portal.ui.components.XpButtonStyle
import com.froyo.apbnsquad.portal.ui.components.XpTextField
import com.froyo.apbnsquad.portal.ui.components.XpWindow
import kotlinx.coroutines.launch

@Composable
fun AdminDialog(
    currentUserId: String,
    token: String,
    supabaseClient: SupabaseClient,
    onDismiss: () -> Unit
) {
    var users by remember { mutableStateOf<List<Profile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    var banningProfile by remember { mutableStateOf<Profile?>(null) }
    var banReasonInput by remember { mutableStateOf("Violation of APBn portal rules") }

    fun refreshUsers() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            val result = supabaseClient.fetchAdminUsers(token)
            if (result.isSuccess) {
                users = result.getOrThrow()
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Could not load users"
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        refreshUsers()
    }

    Dialog(onDismissRequest = onDismiss) {
        XpWindow(
            title = "Admin — Squad Management",
            iconUrl = "file:///android_asset/group-logo.png",
            onClose = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = "Squad Account Administration",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF173A68),
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "Manage portal members, permissions, and moderation bans.",
                    fontSize = 11.sp,
                    color = Color(0xFF5D6D7E),
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                if (banningProfile != null) {
                    // Reason prompt sub-panel
                    val target = banningProfile!!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFECEC))
                            .border(1.dp, Color(0xFFE5A5A5), RoundedCornerShape(4.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "Ban account @${target.username}?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF8A0000)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            XpTextField(
                                value = banReasonInput,
                                onValueChange = { banReasonInput = it },
                                label = "Ban reason",
                                placeholder = "Reason for ban"
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                XpButton(
                                    onClick = { banningProfile = null },
                                    style = XpButtonStyle.SECONDARY
                                ) {
                                    Text("Cancel", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF21384F))
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                XpButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            supabaseClient.toggleBanUser(target.id, true, banReasonInput, token)
                                            banningProfile = null
                                            refreshUsers()
                                        }
                                    },
                                    style = XpButtonStyle.DANGER
                                ) {
                                    Text("Confirm Ban", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C0000))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF1769D1))
                    }
                } else if (!errorMessage.isNullOrBlank()) {
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFF8A0000),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(users) { profile ->
                            val isSelf = profile.id == currentUserId
                            val rowShape = RoundedCornerShape(4.dp)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(rowShape)
                                    .background(Color.White)
                                    .border(1.dp, Color(0xFFC7D7E8), rowShape)
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = profile.effectiveDisplayName,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF173A68)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "@${profile.username}",
                                                fontSize = 10.sp,
                                                color = Color(0xFF5C6F84)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
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

                                            if (profile.banned) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(3.dp))
                                                        .background(Color(0xFFFFCDD2))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = "BANNED",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFB71C1C)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (!isSelf) {
                                        XpButton(
                                            onClick = {
                                                if (profile.banned) {
                                                    coroutineScope.launch {
                                                        supabaseClient.toggleBanUser(profile.id, false, null, token)
                                                        refreshUsers()
                                                    }
                                                } else {
                                                    banningProfile = profile
                                                }
                                            },
                                            style = if (profile.banned) XpButtonStyle.PRIMARY else XpButtonStyle.DANGER
                                        ) {
                                            Text(
                                                text = if (profile.banned) "Unban" else "Ban",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (profile.banned) Color(0xFF102D4B) else Color(0xFF7C0000)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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
