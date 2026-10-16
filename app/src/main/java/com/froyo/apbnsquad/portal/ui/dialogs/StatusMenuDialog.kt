/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.froyo.apbnsquad.portal.data.model.UserStatus

@Composable
fun StatusMenuDialog(
    currentStatus: UserStatus,
    onStatusSelected: (UserStatus) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        val shape = RoundedCornerShape(4.dp)
        Box(
            modifier = Modifier
                .width(180.dp)
                .shadow(8.dp, shape)
                .clip(shape)
                .background(Color(0xFFF9FCFF))
                .border(1.dp, Color(0xFF526B88), shape)
                .padding(vertical = 4.dp)
        ) {
            Column {
                UserStatus.values().forEach { status ->
                    val isSelected = status == currentStatus
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onStatusSelected(status)
                                onDismiss()
                            }
                            .background(if (isSelected) Color(0xFFDCEBFA) else Color.Transparent)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(status.colorHex))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = status.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = Color(0xFF173A68),
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            }
        }
    }
}
