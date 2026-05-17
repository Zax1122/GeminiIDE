package com.alishanj.geminiide.api

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApi {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // conversation history: list of {role, text} pairs
    private val history = mutableListOf<Pair<String, String>>()

    fun clearHistory() {
        history.clear()
    }

    fun sendMessage(apiKey: String, message: String): String {
        return try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

            // Build contents array from full conversation history + new user message
            history.add(Pair("user", message))

            val contentsArray = JSONArray()
            for ((role, text) in history) {
                contentsArray.put(JSONObject().apply {
                    put("role", role)
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", text) })
                    })
                })
            }

            val json = JSONObject().apply {
                put("contents", contentsArray)
            }

            val body = json.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(url).post(body).build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: return "Empty response"

            if (!response.isSuccessful) {
                history.removeLastOrNull() // remove failed user message
                return "API error ${response.code}: $responseBody"
            }

            val jsonResponse = JSONObject(responseBody)
            val replyText = jsonResponse
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            // Add model reply to history
            history.add(Pair("model", replyText))
            replyText

        } catch (e: Exception) {
            history.removeLastOrNull() // remove failed user message
            "Error: ${e.message}"
        }
    }
}
