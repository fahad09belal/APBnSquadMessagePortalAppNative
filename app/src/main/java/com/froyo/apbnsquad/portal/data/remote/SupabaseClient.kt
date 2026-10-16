/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.data.remote

import android.util.Base64
import android.util.Log
import com.froyo.apbnsquad.portal.data.model.Message
import com.froyo.apbnsquad.portal.data.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SupabaseClient {
    companion object {
        const val SUPABASE_URL = "https://exhhtosvrgkvhjighjeg.supabase.co"
        const val SUPABASE_ANON_KEY = "sb_publishable_6woACdWWYM-GxotpgcZ5kA_ny_N8MAf"
        const val AUTH_DOMAIN = "apbn.local"
        const val IMGBB_API_KEY = "d895a576a810d43ebf43d08d566f48a9"
        private const val TAG = "SupabaseClient"

        val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()

        fun authEmail(username: String): String {
            val bytes = username.trim().lowercase().toByteArray(Charsets.UTF_8)
            val base64 = java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
            return "$base64@$AUTH_DOMAIN"
        }
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun signUp(username: String, password: String): Result<Pair<String, String>> =
        withContext(Dispatchers.IO) {
            try {
                val email = authEmail(username)
                val bodyJson = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                    put("data", JSONObject().put("username", username))
                }
                val request = Request.Builder()
                    .url("$SUPABASE_URL/auth/v1/signup")
                    .addHeader("apikey", SUPABASE_ANON_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(bodyJson.toString().toRequestBody(JSON_MEDIA))
                    .build()

                val response = httpClient.newCall(request).execute()
                val responseBody = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    val errJson = try { JSONObject(responseBody) } catch (_: Exception) { null }
                    val message = errJson?.optString("msg")
                        ?: errJson?.optString("error_description")
                        ?: errJson?.optString("message")
                        ?: "Sign up failed: HTTP ${response.code}"
                    return@withContext Result.failure(Exception(message))
                }

                val json = JSONObject(responseBody)
                val sessionObj = json.optJSONObject("session")
                val token = sessionObj?.optString("access_token") ?: json.optString("access_token")
                val userObj = json.optJSONObject("user")
                val userId = userObj?.optString("id") ?: sessionObj?.optJSONObject("user")?.optString("id").orEmpty()

                if (token.isNullOrBlank() || userId.isBlank()) {
                    // Email confirmation enabled or awaiting login
                    return@withContext Result.success(Pair("", userId))
                }

                Result.success(Pair(token, userId))
            } catch (e: Exception) {
                Log.e(TAG, "Sign up error", e)
                Result.failure(e)
            }
        }

    suspend fun signIn(username: String, password: String): Result<Pair<String, String>> =
        withContext(Dispatchers.IO) {
            try {
                val email = authEmail(username)
                val bodyJson = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                }
                val request = Request.Builder()
                    .url("$SUPABASE_URL/auth/v1/token?grant_type=password")
                    .addHeader("apikey", SUPABASE_ANON_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(bodyJson.toString().toRequestBody(JSON_MEDIA))
                    .build()

                val response = httpClient.newCall(request).execute()
                val responseBody = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    val errJson = try { JSONObject(responseBody) } catch (_: Exception) { null }
                    val message = errJson?.optString("error_description")
                        ?: errJson?.optString("msg")
                        ?: "Login failed. Check your username and password."
                    return@withContext Result.failure(Exception(message))
                }

                val json = JSONObject(responseBody)
                val token = json.getString("access_token")
                val userObj = json.getJSONObject("user")
                val userId = userObj.getString("id")

                Result.success(Pair(token, userId))
            } catch (e: Exception) {
                Log.e(TAG, "Sign in error", e)
                Result.failure(e)
            }
        }

    suspend fun checkUsernameExists(username: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val norm = username.lowercase()
                val request = Request.Builder()
                    .url("$SUPABASE_URL/rest/v1/profiles?select=id&username_normalized=eq.$norm")
                    .addHeader("apikey", SUPABASE_ANON_KEY)
                    .get()
                    .build()

                val response = httpClient.newCall(request).execute()
                val responseBody = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    return@withContext Result.success(false)
                }

                val array = JSONArray(responseBody)
                Result.success(array.length() > 0)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun fetchProfile(userId: String, token: String): Result<Profile> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$SUPABASE_URL/rest/v1/profiles?select=*&id=eq.$userId")
                    .addHeader("apikey", SUPABASE_ANON_KEY)
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()

                val response = httpClient.newCall(request).execute()
                val responseBody = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Failed to load profile: ${response.code}"))
                }

                val array = JSONArray(responseBody)
                if (array.length() == 0) {
                    return@withContext Result.failure(Exception("Profile not found"))
                }

                val profile = Profile.fromJson(array.getJSONObject(0))
                Result.success(profile)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun updateProfile(
        userId: String,
        displayName: String?,
        avatarUrl: String?,
        bio: String?,
        token: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("display_name", displayName ?: JSONObject.NULL)
                put("avatar_url", avatarUrl ?: JSONObject.NULL)
                put("bio", bio ?: JSONObject.NULL)
            }

            val request = Request.Builder()
                .url("$SUPABASE_URL/rest/v1/profiles?id=eq.$userId")
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Prefer", "return=minimal")
                .patch(json.toString().toRequestBody(JSON_MEDIA))
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to update profile: HTTP ${response.code}"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchMessages(token: String): Result<List<Message>> =
        withContext(Dispatchers.IO) {
            try {
                val url = "$SUPABASE_URL/rest/v1/messages?select=id,user_id,content,created_at,deleted,reply_to,profiles:user_id(id,username,display_name,avatar_url,bio,role)&order=created_at.asc"
                val request = Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_ANON_KEY)
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()

                val response = httpClient.newCall(request).execute()
                val responseBody = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Failed to fetch messages: ${response.code}"))
                }

                val array = JSONArray(responseBody)
                val messages = mutableListOf<Message>()
                for (i in 0 until array.length()) {
                    messages.add(Message.fromJson(array.getJSONObject(i)))
                }

                // Resolve replies lookup
                val mapById = messages.associateBy { it.id }
                val resolved = messages.map { msg ->
                    if (msg.replyTo != null) {
                        msg.copy(replyTargetMessage = mapById[msg.replyTo])
                    } else {
                        msg
                    }
                }

                Result.success(resolved)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun sendMessage(
        userId: String,
        content: String,
        replyTo: String?,
        token: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("user_id", userId)
                put("content", content)
                if (!replyTo.isNullOrBlank()) {
                    put("reply_to", replyTo)
                }
            }

            val request = Request.Builder()
                .url("$SUPABASE_URL/rest/v1/messages")
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Prefer", "return=minimal")
                .post(json.toString().toRequestBody(JSON_MEDIA))
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                val msg = response.body?.string().orEmpty()
                return@withContext Result.failure(Exception("Could not send message: HTTP ${response.code} $msg"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun softDeleteMessage(
        messageId: String,
        userId: String,
        isAdmin: Boolean,
        token: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val placeholder = if (isAdmin) "[removed by administrator]" else "[deleted]"
            val json = JSONObject().apply {
                put("deleted", true)
                put("content", placeholder)
            }

            var url = "$SUPABASE_URL/rest/v1/messages?id=eq.$messageId"
            if (!isAdmin) {
                url += "&user_id=eq.$userId"
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Prefer", "return=minimal")
                .patch(json.toString().toRequestBody(JSON_MEDIA))
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Could not delete message: HTTP ${response.code}"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAdminUsers(token: String): Result<List<Profile>> =
        withContext(Dispatchers.IO) {
            try {
                val url = "$SUPABASE_URL/rest/v1/profiles?select=id,username,display_name,role,banned,ban_reason,created_at&order=created_at.asc"
                val request = Request.Builder()
                    .url(url)
                    .addHeader("apikey", SUPABASE_ANON_KEY)
                    .addHeader("Authorization", "Bearer $token")
                    .get()
                    .build()

                val response = httpClient.newCall(request).execute()
                val responseBody = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Admin lookup failed: ${response.code}"))
                }

                val array = JSONArray(responseBody)
                val profiles = mutableListOf<Profile>()
                for (i in 0 until array.length()) {
                    profiles.add(Profile.fromJson(array.getJSONObject(i)))
                }

                Result.success(profiles)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun toggleBanUser(
        targetUserId: String,
        ban: Boolean,
        reason: String?,
        token: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("banned", ban)
                put("ban_reason", if (ban) (reason ?: "Violation of APBn portal rules") else JSONObject.NULL)
            }

            val request = Request.Builder()
                .url("$SUPABASE_URL/rest/v1/profiles?id=eq.$targetUserId")
                .addHeader("apikey", SUPABASE_ANON_KEY)
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Prefer", "return=minimal")
                .patch(json.toString().toRequestBody(JSON_MEDIA))
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Could not update ban status: HTTP ${response.code}"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadImageToImgBB(
        imageBytes: ByteArray,
        mimeType: String = "image/jpeg"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val base64 = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
            val formBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", base64)
                .build()

            val request = Request.Builder()
                .url("https://api.imgbb.com/1/upload?key=$IMGBB_API_KEY")
                .post(formBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Image upload failed: HTTP ${response.code}"))
            }

            val json = JSONObject(body)
            val data = json.getJSONObject("data")
            val url = data.getString("url")
            Result.success(url)
        } catch (e: Exception) {
            Log.e(TAG, "ImgBB upload error", e)
            Result.failure(e)
        }
    }
}
