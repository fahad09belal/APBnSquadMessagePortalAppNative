/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.data.local

import android.content.Context
import android.content.SharedPreferences
import com.froyo.apbnsquad.portal.data.model.Profile
import com.froyo.apbnsquad.portal.data.model.UserStatus

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("apbn_squad_portal_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_STATUS = "user_status"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"

        private const val KEY_DISPLAY_NAME = "cached_display_name"
        private const val KEY_AVATAR_URL = "cached_avatar_url"
        private const val KEY_BIO = "cached_bio"
        private const val KEY_ROLE = "cached_role"
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_LAST_KNOWN_MSG_ID = "last_known_msg_id"
    }

    var lastKnownMessageId: String?
        get() = prefs.getString(KEY_LAST_KNOWN_MSG_ID, null)
        set(value) = prefs.edit().putString(KEY_LAST_KNOWN_MSG_ID, value).apply()

    var fcmToken: String?
        get() = prefs.getString(KEY_FCM_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_FCM_TOKEN, value).apply()

    var accessToken: String?
        get() = prefs.getString(KEY_ACCESS_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_ACCESS_TOKEN, value).apply()

    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()

    var username: String?
        get() = prefs.getString(KEY_USERNAME, null)
        set(value) = prefs.edit().putString(KEY_USERNAME, value).apply()

    var status: UserStatus
        get() = UserStatus.fromKey(prefs.getString(KEY_USER_STATUS, "online"))
        set(value) = prefs.edit().putString(KEY_USER_STATUS, value.name.lowercase()).apply()

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()

    val isLoggedIn: Boolean
        get() = !accessToken.isNullOrBlank() && !userId.isNullOrBlank()

    fun saveSession(token: String, uId: String, uName: String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, token)
            .putString(KEY_USER_ID, uId)
            .putString(KEY_USERNAME, uName)
            .apply()
    }

    fun saveProfile(profile: Profile) {
        prefs.edit()
            .putString(KEY_DISPLAY_NAME, profile.displayName)
            .putString(KEY_AVATAR_URL, profile.avatarUrl)
            .putString(KEY_BIO, profile.bio)
            .putString(KEY_ROLE, profile.role)
            .apply()
    }

    fun getCachedProfile(): Profile? {
        val uId = userId ?: return null
        val uName = username ?: return null
        return Profile(
            id = uId,
            username = uName,
            displayName = prefs.getString(KEY_DISPLAY_NAME, null),
            avatarUrl = prefs.getString(KEY_AVATAR_URL, null),
            bio = prefs.getString(KEY_BIO, null),
            role = prefs.getString(KEY_ROLE, "member") ?: "member"
        )
    }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_USER_ID)
            .remove(KEY_USERNAME)
            .remove(KEY_DISPLAY_NAME)
            .remove(KEY_AVATAR_URL)
            .remove(KEY_BIO)
            .remove(KEY_ROLE)
            .remove(KEY_LAST_KNOWN_MSG_ID)
            .apply()
    }
}
