/*
 * Project Froyo — APBn Squad Message Portal
 * Created & Maintained by Fahad Al-Belal
 * Portfolio: https://fahadnway.qd.je
 */

package com.froyo.apbnsquad.portal.data.remote

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Data model for a squad member currently composing a message.
 */
data class TypingUser(
    val userId: String,
    val username: String,
    val displayName: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Real-time typing indicator manager via Supabase Realtime WebSocket broadcast.
 * Provides instant sub-second typing indicator updates across squad members.
 */
class RealtimeTypingManager {

    companion object {
        private const val TAG = "RealtimeTyping"
        private const val TOPIC = "realtime:chat_typing"
        private const val TYPING_TIMEOUT_MS = 3500L
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS) // No read timeout for long-lived persistent WebSocket
        .build()

    private var webSocket: WebSocket? = null
    private var isConnected = false
    private val refCounter = AtomicInteger(1)

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var heartbeatJob: Job? = null
    private var expirationJob: Job? = null

    private val _typingUsers = MutableStateFlow<Map<String, TypingUser>>(emptyMap())
    val typingUsers: StateFlow<Map<String, TypingUser>> = _typingUsers.asStateFlow()

    private var currentUserId: String? = null
    private var isStarted = false

    fun start(userId: String) {
        currentUserId = userId
        if (isStarted) return
        isStarted = true
        connect()
        startPeriodicExpiration()
    }

    fun stop() {
        isStarted = false
        disconnect()
        heartbeatJob?.cancel()
        expirationJob?.cancel()
        _typingUsers.value = emptyMap()
    }

    private fun connect() {
        if (!isStarted) return
        val url = "wss://exhhtosvrgkvhjighjeg.supabase.co/realtime/v1/websocket?apikey=${SupabaseClient.SUPABASE_ANON_KEY}&vsn=1.0.0"
        val request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                Log.d(TAG, "Realtime typing WebSocket connected")
                isConnected = true
                joinTopic(ws)
                startHeartbeat(ws)
            }

            override fun onMessage(ws: WebSocket, text: String) {
                handleIncomingMessage(text)
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                isConnected = false
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                isConnected = false
                if (isStarted) {
                    reconnect()
                }
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                Log.w(TAG, "WebSocket failure: ${t.message}")
                isConnected = false
                if (isStarted) {
                    reconnect()
                }
            }
        })
    }

    private fun reconnect() {
        scope.launch {
            delay(3000)
            if (isStarted && !isConnected) {
                connect()
            }
        }
    }

    private fun disconnect() {
        try {
            webSocket?.close(1000, "User disconnected")
        } catch (_: Exception) {}
        webSocket = null
        isConnected = false
    }

    private fun joinTopic(ws: WebSocket) {
        val joinPayload = JSONObject().apply {
            put("topic", TOPIC)
            put("event", "phx_join")
            put("payload", JSONObject().apply {
                put("config", JSONObject().apply {
                    put("broadcast", JSONObject().apply {
                        put("ack", false)
                        put("self", false)
                    })
                })
            })
            put("ref", refCounter.incrementAndGet().toString())
        }
        ws.send(joinPayload.toString())
    }

    private fun startHeartbeat(ws: WebSocket) {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            while (isActive && isConnected) {
                delay(20000)
                val hb = JSONObject().apply {
                    put("topic", "phoenix")
                    put("event", "heartbeat")
                    put("payload", JSONObject())
                    put("ref", refCounter.incrementAndGet().toString())
                }
                ws.send(hb.toString())
            }
        }
    }

    fun sendTyping(
        userId: String,
        username: String,
        displayName: String,
        isTyping: Boolean
    ) {
        val ws = webSocket ?: return
        if (!isConnected) return

        try {
            val payload = JSONObject().apply {
                put("userId", userId)
                put("username", username)
                put("displayName", displayName)
                put("isTyping", isTyping)
                put("timestamp", System.currentTimeMillis())
            }
            val broadcastMsg = JSONObject().apply {
                put("topic", TOPIC)
                put("event", "broadcast")
                put("payload", JSONObject().apply {
                    put("type", "broadcast")
                    put("event", "typing")
                    put("payload", payload)
                })
                put("ref", refCounter.incrementAndGet().toString())
            }
            ws.send(broadcastMsg.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Error sending typing status", e)
        }
    }

    private fun handleIncomingMessage(text: String) {
        try {
            val json = JSONObject(text)
            val event = json.optString("event")
            if (event == "broadcast") {
                val payloadOuter = json.optJSONObject("payload") ?: return
                val eventType = payloadOuter.optString("event")
                if (eventType == "typing") {
                    val data = payloadOuter.optJSONObject("payload") ?: return
                    val senderId = data.optString("userId")
                    val isTyping = data.optBoolean("isTyping", false)
                    val username = data.optString("username")
                    val displayName = data.optString("displayName", username)

                    if (senderId.isNotBlank() && senderId != currentUserId) {
                        val currentMap = _typingUsers.value.toMutableMap()
                        if (isTyping) {
                            currentMap[senderId] = TypingUser(
                                userId = senderId,
                                username = username,
                                displayName = displayName,
                                timestamp = System.currentTimeMillis()
                            )
                        } else {
                            currentMap.remove(senderId)
                        }
                        _typingUsers.value = currentMap
                    }
                }
            }
        } catch (_: Exception) {}
    }

    private fun startPeriodicExpiration() {
        expirationJob?.cancel()
        expirationJob = scope.launch {
            while (isActive) {
                delay(1500)
                val now = System.currentTimeMillis()
                val currentMap = _typingUsers.value
                val filtered = currentMap.filter { (_, user) ->
                    (now - user.timestamp) < TYPING_TIMEOUT_MS
                }
                if (filtered.size != currentMap.size) {
                    _typingUsers.value = filtered
                }
            }
        }
    }
}
