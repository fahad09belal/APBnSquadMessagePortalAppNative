/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 * Portfolio: https://fahadnway.qd.je
 */

package com.froyo.apbnsquad.portal.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.froyo.apbnsquad.portal.data.local.SessionManager

/**
 * Boot and Alarm receiver for APBn Squad Message Portal.
 * Restarts background notification monitoring when device boots or alarm triggers.
 */
class BootReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_KEEP_ALIVE = "com.froyo.apbnsquad.portal.ACTION_KEEP_ALIVE"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val sessionManager = SessionManager(context)
        if (sessionManager.isLoggedIn && sessionManager.notificationsEnabled) {
            PortalBackgroundService.start(context)
        }
    }
}
