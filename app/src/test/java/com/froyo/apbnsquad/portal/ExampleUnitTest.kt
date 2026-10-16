/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 */

package com.froyo.apbnsquad.portal

import com.froyo.apbnsquad.portal.data.model.UserStatus
import com.froyo.apbnsquad.portal.data.remote.SupabaseClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testAuthEmailGeneration() {
        val email = SupabaseClient.authEmail("FAHAD@001")
        assertTrue("Email must end with @apbn.local", email.endsWith("@apbn.local"))
        // Base64url without padding
        assertTrue("Email prefix must not contain padding '='", !email.substringBefore("@").contains("="))
    }

    @Test
    fun testUserStatusParsing() {
        assertEquals(UserStatus.ONLINE, UserStatus.fromKey("online"))
        assertEquals(UserStatus.DND, UserStatus.fromKey("dnd"))
        assertEquals(UserStatus.IDLE, UserStatus.fromKey("idle"))
        assertEquals(UserStatus.ONLINE, UserStatus.fromKey("unknown"))
    }
}
