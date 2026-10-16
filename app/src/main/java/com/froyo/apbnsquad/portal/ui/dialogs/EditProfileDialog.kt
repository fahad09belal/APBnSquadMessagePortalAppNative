/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.ui.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
fun EditProfileDialog(
    currentProfile: Profile?,
    supabaseClient: SupabaseClient,
    onSave: (displayName: String?, avatarUrl: String?, bio: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var displayName by remember { mutableStateOf(currentProfile?.displayName.orEmpty()) }
    var avatarUrl by remember { mutableStateOf(currentProfile?.avatarUrl.orEmpty()) }
    var bio by remember { mutableStateOf(currentProfile?.bio.orEmpty()) }
    var isUploadingImage by remember { mutableStateOf(false) }
    var uploadStatusMsg by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                isUploadingImage = true
                uploadStatusMsg = "Uploading picture to ImgBB…"
                try {
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    if (bytes != null) {
                        val result = supabaseClient.uploadImageToImgBB(bytes)
                        if (result.isSuccess) {
                            val newUrl = result.getOrThrow()
                            avatarUrl = newUrl
                            uploadStatusMsg = "Uploaded successfully!"
                        } else {
                            uploadStatusMsg = "Upload failed: ${result.exceptionOrNull()?.message}"
                        }
                    }
                } catch (e: Exception) {
                    uploadStatusMsg = "Error reading image: ${e.message}"
                } finally {
                    isUploadingImage = false
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        XpWindow(
            title = "Edit Profile",
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
                    text = "Edit Profile",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF173A68),
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "Update your personal details visible to the squad.",
                    fontSize = 11.sp,
                    color = Color(0xFF5D6D7E),
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                XpTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = "Display Name",
                    placeholder = "e.g. Commander Alex"
                )

                Spacer(modifier = Modifier.height(8.dp))

                XpTextField(
                    value = avatarUrl,
                    onValueChange = { avatarUrl = it },
                    label = "Avatar Image URL",
                    placeholder = "https://example.com/avatar.png"
                )

                Spacer(modifier = Modifier.height(6.dp))

                XpButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    style = XpButtonStyle.SECONDARY,
                    enabled = !isUploadingImage,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isUploadingImage) "Uploading picture…" else "Upload Profile Picture",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF21384F)
                    )
                }

                if (!uploadStatusMsg.isNullOrBlank()) {
                    Text(
                        text = uploadStatusMsg!!,
                        fontSize = 10.sp,
                        color = if (uploadStatusMsg!!.contains("success", ignoreCase = true)) Color(0xFF1B7330) else Color(0xFF8A0000),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                XpTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = "Bio",
                    placeholder = "Tell the squad about yourself...",
                    singleLine = false,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                XpButton(
                    onClick = {
                        onSave(displayName.trim().ifEmpty { null }, avatarUrl.trim().ifEmpty { null }, bio.trim().ifEmpty { null })
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Save Changes",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF102D4B)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                XpButton(
                    onClick = onDismiss,
                    style = XpButtonStyle.SECONDARY,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF21384F)
                    )
                }
            }
        }
    }
}
