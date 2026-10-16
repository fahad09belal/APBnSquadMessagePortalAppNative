/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 * Portfolio: https://fahadnway.qd.je
 */

package com.froyo.apbnsquad.portal

import android.app.Application
import com.froyo.apbnsquad.portal.service.NotificationHelper

/**
 * Base Application class for Project Froyo Portal.
 */
class PortalApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            NotificationHelper.createNotificationChannels(this)
            com.froyo.apbnsquad.portal.service.PortalFirebaseMessagingService.initializeFcm(this)
            val session = com.froyo.apbnsquad.portal.data.local.SessionManager(this)
            if (session.isLoggedIn && session.notificationsEnabled) {
                com.froyo.apbnsquad.portal.service.PortalBackgroundService.start(this)
            }
        } catch (_: Throwable) {
        }
    }
}
