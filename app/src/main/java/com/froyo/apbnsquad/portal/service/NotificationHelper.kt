/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 * Portfolio: https://fahadnway.qd.je
 */

package com.froyo.apbnsquad.portal.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.froyo.apbnsquad.portal.MainActivity
import com.froyo.apbnsquad.portal.R
import java.util.concurrent.atomic.AtomicInteger

/**
 * Handles creation of notification channels and delivery of message notifications.
 * Authored by Fahad Al-Belal for Project Froyo.
 */
object NotificationHelper {
    const val CHANNEL_MESSAGES_ID = "apbn_portal_messages"
    const val CHANNEL_BACKGROUND_ID = "apbn_portal_sync"
    private const val NOTIFICATION_GROUP_KEY = "com.froyo.apbnsquad.portal.MESSAGES"

    private val notificationIdCounter = AtomicInteger(1000)

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // High priority channel for real-time incoming messages with sound & vibration
            val messageChannel = NotificationChannel(
                CHANNEL_MESSAGES_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 150, 250)
                setShowBadge(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            manager.createNotificationChannel(messageChannel)

            // Low priority channel for ongoing background monitoring (quiet, non-intrusive)
            val backgroundChannel = NotificationChannel(
                CHANNEL_BACKGROUND_ID,
                context.getString(R.string.sync_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.sync_channel_desc)
                enableVibration(false)
                setShowBadge(false)
            }
            manager.createNotificationChannel(backgroundChannel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showMessageNotification(
        context: Context,
        title: String,
        body: String
    ) {
        if (!hasNotificationPermission(context)) {
            return
        }

        val cleanTitle = title.trim().ifEmpty { "APBn Squad Message" }
        var cleanBody = body.trim()

        // Strip raw URLs or domains if redundant in body
        cleanBody = cleanBody
            .replace("https://apbnsquad.qd.je", "")
            .replace("http://apbnsquad.qd.je", "")
            .replace("apbnsquad.qd.je", "")
            .trim()

        if (cleanBody.isEmpty()) {
            return
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val id = notificationIdCounter.incrementAndGet()

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Large icon shows the portal squad branding on the right
        val largeIcon = try {
            BitmapFactory.decodeResource(context.resources, R.drawable.app_icon)
        } catch (_: Exception) {
            null
        }

        // Small icon uses a crisp white silhouette chat message icon with accent color
        val notification = NotificationCompat.Builder(context, CHANNEL_MESSAGES_ID)
            .setSmallIcon(R.drawable.ic_notification_message)
            .setColor(0xFF1769D1.toInt()) // APBn Squad blue accent tint
            .apply {
                if (largeIcon != null) {
                    setLargeIcon(largeIcon)
                }
            }
            .setContentTitle(cleanTitle)
            .setContentText(cleanBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cleanBody))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setGroup(NOTIFICATION_GROUP_KEY)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(id, notification)
    }
}
