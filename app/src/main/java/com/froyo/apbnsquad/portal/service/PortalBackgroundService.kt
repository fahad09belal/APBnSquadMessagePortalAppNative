/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 * Portfolio: https://fahadnway.qd.je
 */

package com.froyo.apbnsquad.portal.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.froyo.apbnsquad.portal.MainActivity
import com.froyo.apbnsquad.portal.R
import com.froyo.apbnsquad.portal.data.local.SessionManager
import com.froyo.apbnsquad.portal.data.remote.SupabaseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Background Service for APBn Squad Message Portal.
 * Keeps running when the app is closed so incoming squad messages
 * trigger push notifications immediately.
 */
class PortalBackgroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var pollJob: Job? = null
    private val supabaseClient = SupabaseClient()

    companion object {
        private const val TAG = "PortalBgService"
        private const val NOTIFICATION_ID_FOREGROUND = 8881
        private const val POLL_INTERVAL_MS = 4000L

        fun start(context: Context) {
            val sessionManager = SessionManager(context)
            if (!sessionManager.isLoggedIn || !sessionManager.notificationsEnabled) {
                return
            }
            val intent = Intent(context, PortalBackgroundService::class.java)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to start background service: ${e.message}")
            }
        }

        fun stop(context: Context) {
            try {
                val intent = Intent(context, PortalBackgroundService::class.java)
                context.stopService(intent)
            } catch (_: Exception) {}
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
        startInForeground()
        scheduleKeepAliveAlarm(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sessionManager = SessionManager(applicationContext)
        if (!sessionManager.isLoggedIn || !sessionManager.notificationsEnabled) {
            stopSelf()
            return START_NOT_STICKY
        }

        startInForeground()
        startMessagePollLoop()
        return START_STICKY
    }

    private fun startInForeground() {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, NotificationHelper.CHANNEL_BACKGROUND_ID)
            .setSmallIcon(R.drawable.ic_notification_message)
            .setContentTitle(getString(R.string.service_notification_title))
            .setContentText(getString(R.string.service_notification_desc))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID_FOREGROUND,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID_FOREGROUND, notification)
        }
    }

    private fun startMessagePollLoop() {
        pollJob?.cancel()
        pollJob = serviceScope.launch {
            val sessionManager = SessionManager(applicationContext)
            while (isActive) {
                try {
                    val token = sessionManager.accessToken
                    val currentUserId = sessionManager.userId

                    if (!token.isNullOrBlank() && !currentUserId.isNullOrBlank() && sessionManager.notificationsEnabled) {
                        val result = supabaseClient.fetchMessages(token)
                        if (result.isSuccess) {
                            val messages = result.getOrThrow()
                            val lastKnownId = sessionManager.lastKnownMessageId
                            val latestMsg = messages.lastOrNull()

                            if (latestMsg != null) {
                                if (lastKnownId == null) {
                                    // Initial baseline
                                    sessionManager.lastKnownMessageId = latestMsg.id
                                } else if (latestMsg.id != lastKnownId) {
                                    val newMessages = messages.takeLastWhile { it.id != lastKnownId }
                                    for (msg in newMessages) {
                                        if (msg.userId != currentUserId && !msg.deleted) {
                                            val author = msg.authorProfile?.effectiveDisplayName ?: "Squad Member"
                                            NotificationHelper.showMessageNotification(
                                                context = applicationContext,
                                                title = author,
                                                body = msg.content
                                            )
                                        }
                                    }
                                    sessionManager.lastKnownMessageId = latestMsg.id
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Background polling exception: ${e.message}")
                }

                delay(POLL_INTERVAL_MS)
            }
        }
    }

    private fun scheduleKeepAliveAlarm(context: Context) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
            val alarmIntent = Intent(context, BootReceiver::class.java).apply {
                action = BootReceiver.ACTION_KEEP_ALIVE
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                999,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerAt = SystemClock.elapsedRealtime() + 10 * 60 * 1000 // 10 minutes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent)
            }
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        pollJob?.cancel()
        val sessionManager = SessionManager(applicationContext)
        if (sessionManager.isLoggedIn && sessionManager.notificationsEnabled) {
            scheduleKeepAliveAlarm(applicationContext)
        }
        super.onDestroy()
    }
}
