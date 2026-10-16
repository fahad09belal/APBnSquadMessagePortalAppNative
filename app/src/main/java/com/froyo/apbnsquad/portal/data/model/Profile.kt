/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.data.model

import org.json.JSONObject

data class Profile(
    val id: String,
    val username: String,
    val usernameNormalized: String = username.lowercase(),
    val displayName: String? = null,
    val avatarUrl: String? = null,
    val bio: String? = null,
    val role: String = "member", // "member" or "admin"
    val banned: Boolean = false,
    val banReason: String? = null,
    val createdAt: String? = null
) {
    val effectiveDisplayName: String
        get() = displayName?.takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) } ?: username

    val effectiveAvatarUrl: String
        get() = avatarUrl?.takeIf { it.isNotBlank() && !it.equals("null", ignoreCase = true) } ?: "file:///android_asset/group-logo.png"

    val isAdmin: Boolean
        get() = role.equals("admin", ignoreCase = true)

    companion object {
        fun fromJson(json: JSONObject): Profile {
            fun optCleanString(key: String): String? {
                if (json.isNull(key)) return null
                val str = json.optString(key, "").trim()
                return if (str.isEmpty() || str.equals("null", ignoreCase = true)) null else str
            }

            val username = optCleanString("username").orEmpty()
            return Profile(
                id = json.optString("id", ""),
                username = username,
                usernameNormalized = optCleanString("username_normalized") ?: username.lowercase(),
                displayName = optCleanString("display_name"),
                avatarUrl = optCleanString("avatar_url"),
                bio = optCleanString("bio"),
                role = optCleanString("role") ?: "member",
                banned = json.optBoolean("banned", false),
                banReason = optCleanString("ban_reason"),
                createdAt = optCleanString("created_at")
            )
        }
    }
}
