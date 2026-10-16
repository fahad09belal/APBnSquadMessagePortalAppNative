/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.service

import android.content.Context
import android.util.Log
import com.froyo.apbnsquad.portal.data.local.SessionManager
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PortalFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val sessionManager = SessionManager(applicationContext)
        if (!sessionManager.notificationsEnabled) {
            return
        }

        // Extract title and body from either notification payload or data payload
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "APBn Squad"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: remoteMessage.data["content"]
            ?: remoteMessage.data["message"]
            ?: ""

        if (body.isNotBlank()) {
            NotificationHelper.showMessageNotification(
                context = applicationContext,
                title = title,
                body = body
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Registration Token: $token")
        val sessionManager = SessionManager(applicationContext)
        sessionManager.fcmToken = token
    }

    companion object {
        private const val TAG = "PortalFCMService"

        fun initializeFcm(context: Context) {
            try {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        Log.d(TAG, "FCM Token initialized: $token")
                        SessionManager(context).fcmToken = token
                    } else {
                        Log.w(TAG, "Fetching FCM token failed", task.exception)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Firebase messaging initialization deferred: ${e.message}")
            }
        }
    }
}
