/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.data.model

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class Message(
    val id: String,
    val userId: String,
    val content: String,
    val createdAt: String,
    val deleted: Boolean = false,
    val replyTo: String? = null,
    val authorProfile: Profile? = null,
    val replyTargetMessage: Message? = null
) {
    val formattedTime: String
        get() {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                // Handle potential fractional seconds or timezone suffixes
                val sanitized = if (createdAt.contains(".")) {
                    createdAt.substringBefore(".")
                } else {
                    createdAt.substringBefore("+").substringBefore("Z")
                }
                val date = inputFormat.parse(sanitized) ?: Date()
                val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                outputFormat.format(date)
            } catch (_: Exception) {
                ""
            }
        }

    companion object {
        fun fromJson(json: JSONObject): Message {
            val profileJson = json.optJSONObject("profiles")
            val author = profileJson?.let { Profile.fromJson(it) }

            return Message(
                id = json.optString("id", ""),
                userId = json.optString("user_id", ""),
                content = json.optString("content", ""),
                createdAt = json.optString("created_at", ""),
                deleted = json.optBoolean("deleted", false),
                replyTo = json.optString("reply_to", "").takeIf { it.isNotBlank() },
                authorProfile = author
            )
        }
    }
}
