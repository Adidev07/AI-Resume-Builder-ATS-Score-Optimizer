package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ResumeContent
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    
    // OkHttp Client configured with 60s timeouts as mandated for Gemini calls
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    // Retrieve API key securely from BuildConfig
    private val apiKey: String
        get() = BuildConfig.GEMINI_API_KEY

    /**
     * Helper to make a direct REST API call to Gemini 3.5 Flash
     */
    private suspend fun callGemini(systemInstruction: String, userPrompt: String): String? = withContext(Dispatchers.IO) {
        val key = apiKey
        if (key.isEmpty() || key == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API key is empty or default placeholder! Please insert key in AI Studio Secrets panel.")
            return@withContext null
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$key"
        
        val jsonRequest = JSONObject().apply {
            put("contents", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", org.json.JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", userPrompt)
                        })
                    })
                })
            })
            if (systemInstruction.isNotEmpty()) {
                put("systemInstruction", JSONObject().apply {
                    put("parts", org.json.JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", systemInstruction)
                        })
                    })
                })
            }
            // Ask for JSON response format
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.7)
            })
        }

        val body = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "Unsuccessful response from Gemini API: code=${response.code}, body=${response.body?.string()}")
                    return@withContext null
                }
                val responseBodyStr = response.body?.string() ?: return@withContext null
                val rootJson = JSONObject(responseBodyStr)
                val candidates = rootJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text")
                    }
                }
                null
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network exception calling Gemini", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "General exception calling Gemini", e)
            null
        }
    }

    /**
     * Feature 2: Improve resume based on Job Description
     */
    suspend fun improveResume(originalContent: ResumeContent, jobDescription: String): ResumeContent? {
        val originalJson = JsonSerializer.serializeResumeContent(originalContent)
        val systemInstruction = "You are an expert resume writer and career coach. Improve this resume for the given job description. Make experience bullet points specific and quantified. Match keywords naturally from the job description. Keep it completely truthful - only improve framing, do not fabricate. Output ONLY a valid JSON object matching the input structure exactly."
        val userPrompt = "ORIGINAL RESUME:\n$originalJson\n\nTARGET JOB DESCRIPTION:\n$jobDescription\n\nProvide the improved resume in the exact same JSON format."

        val responseText = callGemini(systemInstruction, userPrompt) ?: return null
        return try {
            JsonSerializer.deserializeResumeContent(responseText)
        } catch (e: Exception) {
            Log.e(TAG, "Could not parse improved resume JSON", e)
            null
        }
    }

    /**
     * Feature 3: ATS Score Checker
     */
    suspend fun checkAtsScore(resumeText: String, jobDescription: String): String? {
        val systemInstruction = """
            You are an expert ATS (Applicant Tracking System) analyzer.
            Analyze the given resume text against the target job description. Return ONLY valid JSON with this exact structure:
            {
              "score": 85,
              "keywordMatch": {
                "found": ["Kotlin", "Jetpack Compose", "Room Database"],
                "missing": ["Coroutines", "Dagger Hilt"],
                "matchPct": 60
              },
              "sections": {
                "contact": 100,
                "summary": 90,
                "experience": 80,
                "skills": 85,
                "education": 100
              },
              "formattingIssues": [
                "Avoid using double column layout as some basic ATS parsers cannot read it in order.",
                "Ensure all work experience listings use bullet points rather than dense paragraphs."
              ],
              "topSuggestions": [
                "Incorporate the keyword 'Coroutines' into your Android Developer role bullet points.",
                "Quantify your accomplishments: add metrics such as performance increase % or team sizes."
              ],
              "verdict": "Strong Match"
            }
        """.trimIndent()

        val userPrompt = "RESUME TEXT:\n$resumeText\n\nTARGET JOB DESCRIPTION:\n$jobDescription\n\nPerform full ATS scoring and return the exact JSON."
        return callGemini(systemInstruction, userPrompt)
    }

    /**
     * Feature 4: Cover Letter Generator
     */
    suspend fun generateCoverLetter(companyName: String, role: String, topAchievements: List<String>, tone: String): String? {
        val achievementsList = topAchievements.joinToString("\n") { "- $it" }
        val systemInstruction = "You are a professional hiring consultant. Write a highly tailored, persuasive 3-paragraph cover letter based on the inputs."
        val userPrompt = """
            Write a 3-paragraph cover letter.
            Company: $companyName
            Role: $role
            Tone: $tone
            Top User Achievements to Highlight:
            $achievementsList
            
            Format:
            Output ONLY a JSON object with a single string field "coverLetter" containing the formatted letter.
        """.trimIndent()

        val responseText = callGemini(systemInstruction, userPrompt) ?: return null
        return try {
            val json = JSONObject(responseText)
            json.optString("coverLetter")
        } catch (e: Exception) {
            Log.e(TAG, "Could not parse cover letter response", e)
            null
        }
    }
}
