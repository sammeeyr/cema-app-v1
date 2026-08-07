package com.example.data.repository

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAiRepository {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun askGemini(prompt: String, systemPrompt: String? = null): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateLocalAiResponse(prompt)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite:generateContent?key=$apiKey"

            val partObj = JSONObject().put("text", prompt)
            val partsArray = JSONArray().put(partObj)
            val contentObj = JSONObject().put("parts", partsArray)
            val contentsArray = JSONArray().put(contentObj)

            val rootJson = JSONObject().put("contents", contentsArray)

            val sysText = systemPrompt ?: "You are CEMA Bible AI Assistant. Provide inspiring, concise, scripturally grounded, and encouraging theological explanations."
            val sysPartObj = JSONObject().put("text", sysText)
            val sysPartsArray = JSONArray().put(sysPartObj)
            val sysContentObj = JSONObject().put("parts", sysPartsArray)
            rootJson.put("systemInstruction", sysContentObj)

            val requestBody = rootJson.toString().toRequestBody("application/json".toMediaType())

            val httpRequest = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(httpRequest).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val responseObj = JSONObject(responseBody)
                val candidates = responseObj.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCand = candidates.getJSONObject(0)
                    val candContent = firstCand.optJSONObject("content")
                    val candParts = candContent?.optJSONArray("parts")
                    if (candParts != null && candParts.length() > 0) {
                        val text = candParts.getJSONObject(0).optString("text")
                        if (!text.isNullOrEmpty()) {
                            return@withContext text
                        }
                    }
                }
            }

            return@withContext generateLocalAiResponse(prompt)
        } catch (e: Exception) {
            return@withContext generateLocalAiResponse(prompt)
        }
    }

    private fun generateLocalAiResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("justification") -> {
                "Justification in Scripture means to be declared righteous before God. Through faith in Jesus Christ (Romans 5:1), our sins are erased and Christ's righteousness is credited to our account, giving us full peace with God."
            }
            lower.contains("lesson") || lower.contains("summarize") -> {
                "Lesson Summary: True spiritual growth begins with understanding Christ's unmerited love (John 3:16). Walking by faith requires surrendering personal control, trusting His promises daily, and maintaining communion through prayer."
            }
            lower.contains("prayer") -> {
                "Heavenly Father, I thank You for Your unending grace and the gift of Your Holy Spirit. Strengthen my heart today to walk in faith, speak with kindness, and glorify Your holy name in all that I do. In Jesus' name, Amen."
            }
            lower.contains("application") || lower.contains("practical") -> {
                "Practical Application:\n1. Dedicate 15 minutes each morning to scripture meditation.\n2. Journal 3 things you are grateful for today.\n3. Encourage a friend or brother with an uplifting verse."
            }
            lower.contains("explain") -> {
                "Scriptural Insight: This passage highlights God's sovereign covenant love. It reminds us that our security is not based on temporary circumstances, but on Christ's eternal redemption."
            }
            else -> {
                "CEMA Bible Study Companion:\nGod's Word is a lamp unto your feet and a light unto your path. Take time today to ponder His promises, pray with filial trust, and walk in the freedom of His grace."
            }
        }
    }
}
