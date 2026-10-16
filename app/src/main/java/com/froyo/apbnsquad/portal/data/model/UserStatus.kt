/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal.data.model

enum class UserStatus(val label: String, val assetName: String, val colorHex: Long) {
    ONLINE("Online", "online.png", 0xFF2AA84A),
    DND("DND", "dnd.png", 0xFFC60000),
    IDLE("Idle", "idle.png", 0xFFF57C00);

    val assetUrl: String
        get() = "file:///android_asset/$assetName"

    companion object {
        fun fromKey(key: String?): UserStatus {
            return when (key?.lowercase()) {
                "dnd" -> DND
                "idle" -> IDLE
                else -> ONLINE
            }
        }
    }
}
