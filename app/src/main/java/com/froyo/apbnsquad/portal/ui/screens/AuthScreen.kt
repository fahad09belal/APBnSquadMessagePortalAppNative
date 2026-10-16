/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.froyo.apbnsquad.portal.ui.components.XpButton
import com.froyo.apbnsquad.portal.ui.components.XpTaskbar
import com.froyo.apbnsquad.portal.ui.components.XpTextField
import com.froyo.apbnsquad.portal.ui.components.XpWindow

@Composable
fun AuthScreen(
    onSignIn: (username: String, password: String, onError: (String) -> Unit) -> Unit,
    onSignUp: (username: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) -> Unit,
    onOpenAboutUs: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUpMode by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isStatusGood by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val usernameRegex = remember { Regex("^[A-Za-z]+@[0-9]+$") }
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF041C3D))
    ) {
        // Desktop Wallpaper
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/background.png")
                .crossfade(false)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Centered Auth Window
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 44.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            XpWindow(
                title = "APBn Squad Message Portal",
                iconUrl = "file:///android_asset/custom-logo.png",
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Header with Squad Logo
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data("file:///android_asset/group-logo.png")
                                .crossfade(false)
                                .build(),
                            contentDescription = "APBn Squad",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFF91A8BF), RoundedCornerShape(8.dp))
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "APBn Squad",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF173A68),
                                fontFamily = FontFamily.SansSerif
                            )
                            Text(
                                text = "Message Portal",
                                fontSize = 12.sp,
                                color = Color(0xFF5C6F84),
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }

                    if (!isSignUpMode) {
                        // Sign In Mode
                        Text(
                            text = "Sign in",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF173A68),
                            fontFamily = FontFamily.SansSerif
                        )
                        Text(
                            text = "Enter your APBn account to continue.",
                            fontSize = 12.sp,
                            color = Color(0xFF5D6D7E),
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        XpTextField(
                            value = username,
                            onValueChange = {
                                username = it
                                statusMessage = null
                            },
                            label = "Username",
                            placeholder = "alphabet@number"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        XpTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                statusMessage = null
                            },
                            label = "Password",
                            placeholder = "Password",
                            isPassword = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        XpButton(
                            onClick = {
                                val trimmed = username.trim()
                                if (!usernameRegex.matches(trimmed)) {
                                    statusMessage = "Enter a username like alphabet@number."
                                    isStatusGood = false
                                    return@XpButton
                                }
                                if (password.isEmpty()) {
                                    statusMessage = "Enter your password."
                                    isStatusGood = false
                                    return@XpButton
                                }
                                isLoading = true
                                statusMessage = null
                                onSignIn(trimmed, password) { error ->
                                    isLoading = false
                                    statusMessage = error
                                    isStatusGood = false
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF102D4B)
                                )
                            } else {
                                Text(
                                    text = "Sign in",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF102D4B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "New to the portal? ",
                                fontSize = 11.sp,
                                color = Color(0xFF5C6D80),
                                fontFamily = FontFamily.SansSerif
                            )
                            Text(
                                text = "Create an account",
                                fontSize = 11.sp,
                                color = Color(0xFF064FA9),
                                textDecoration = TextDecoration.Underline,
                                fontFamily = FontFamily.SansSerif,
                                modifier = Modifier.clickable {
                                    isSignUpMode = true
                                    statusMessage = null
                                }
                            )
                        }
                    } else {
                        // Sign Up Mode
                        Text(
                            text = "Create account",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF173A68),
                            fontFamily = FontFamily.SansSerif
                        )
                        Text(
                            text = "Username format: alphabet@number.",
                            fontSize = 12.sp,
                            color = Color(0xFF5D6D7E),
                            fontFamily = FontFamily.SansSerif,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        XpTextField(
                            value = username,
                            onValueChange = {
                                username = it
                                statusMessage = null
                            },
                            label = "Username",
                            placeholder = "alphabet@number"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        XpTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                statusMessage = null
                            },
                            label = "Password",
                            placeholder = "Minimum 6 characters",
                            isPassword = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        XpTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                statusMessage = null
                            },
                            label = "Confirm password",
                            placeholder = "Repeat password",
                            isPassword = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        XpButton(
                            onClick = {
                                val trimmed = username.trim()
                                if (!usernameRegex.matches(trimmed)) {
                                    statusMessage = "Username must look like alphabet@number."
                                    isStatusGood = false
                                    return@XpButton
                                }
                                if (password.length < 6) {
                                    statusMessage = "Password must be at least 6 characters."
                                    isStatusGood = false
                                    return@XpButton
                                }
                                if (password != confirmPassword) {
                                    statusMessage = "Passwords do not match."
                                    isStatusGood = false
                                    return@XpButton
                                }
                                isLoading = true
                                statusMessage = null
                                onSignUp(
                                    trimmed,
                                    password,
                                    { msg ->
                                        isLoading = false
                                        statusMessage = msg
                                        isStatusGood = true
                                        isSignUpMode = false
                                    },
                                    { error ->
                                        isLoading = false
                                        statusMessage = error
                                        isStatusGood = false
                                    }
                                )
                            },
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF102D4B)
                                )
                            } else {
                                Text(
                                    text = "Create account",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF102D4B)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Already registered? ",
                                fontSize = 11.sp,
                                color = Color(0xFF5C6D80),
                                fontFamily = FontFamily.SansSerif
                            )
                            Text(
                                text = "Sign in",
                                fontSize = 11.sp,
                                color = Color(0xFF064FA9),
                                textDecoration = TextDecoration.Underline,
                                fontFamily = FontFamily.SansSerif,
                                modifier = Modifier.clickable {
                                    isSignUpMode = false
                                    statusMessage = null
                                }
                            )
                        }
                    }

                    // Live Status Box
                    if (!statusMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        val statusShape = RoundedCornerShape(3.dp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(statusShape)
                                .background(if (isStatusGood) Color(0xFFE1F5E5) else Color(0xFFFFE2E2))
                                .border(
                                    1.dp,
                                    if (isStatusGood) Color(0xFF8ABF93) else Color(0xFFCF8D8D),
                                    statusShape
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = statusMessage!!,
                                fontSize = 11.sp,
                                color = if (isStatusGood) Color(0xFF126022) else Color(0xFF8A0000),
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }
                }
            }
        }

        // Bottom Windows XP Taskbar
        XpTaskbar(
            onStartClick = onOpenAboutUs,
            onWindowTabClick = {},
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
