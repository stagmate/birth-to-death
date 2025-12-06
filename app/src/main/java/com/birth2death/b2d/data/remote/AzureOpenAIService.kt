package com.birth2death.b2d.data.remote

import android.util.Log
import com.birth2death.b2d.data.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

object AzureOpenAIService {
    private const val TAG = "AzureOpenAIService"
    // Use 10.0.2.2 for Android Emulator to access host localhost
    private const val BASE_URL = "http://10.0.2.2:4000/" 
    
    private val api: ApiService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiService::class.java)
    }

    suspend fun generateSubtasks(
        taskTitle: String,
        description: String? = null,
        emotionalState: EmotionalState = EmotionalState.NEUTRAL,
        difficulty: Difficulty = Difficulty.MEDIUM
    ): List<Subtask> = withContext(Dispatchers.IO) {
        val query = """
        mutation GenerateSubtasks {
            generateSubtasks(
                taskTitle: "${escapeString(taskTitle)}",
                taskDescription: "${escapeString(description ?: "")}",
                emotionalState: ${emotionalState.name},
                difficulty: ${difficulty.name}
            ) {
                id
                title
                isCompleted
                order
                estimatedMinutes
            }
        }
        """.trimIndent()

        try {
            val response = api.query(GraphQLRequest(query))
            // Parse response manually due to dynamic GraphQL nature or use strict types if defined
             val data = response.data
             if (data != null) {
                 val json = Gson().toJson(data)
                 val type = object : TypeToken<Map<String, Any>>() {}.type
                 val map: Map<String, Any> = Gson().fromJson(json, type)
                 
                 val subtasksRaw = (map["generateSubtasks"] as? List<Map<String, Any>>)
                 if (subtasksRaw != null) {
                     return@withContext subtasksRaw.map { 
                         Subtask(
                             id = it["id"] as? String ?: UUID.randomUUID().toString(),
                             title = it["title"] as? String ?: "Untitled",
                             isCompleted = it["isCompleted"] as? Boolean ?: false
                         )
                     }
                 }
             }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate subtasks", e)
        }
        return@withContext generateFallbackSubtasks(taskTitle)
    }

    private fun escapeString(s: String): String {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n")
    }

    private fun generateFallbackSubtasks(taskTitle: String): List<Subtask> {
        return listOf(
            Subtask(title = "Understand the requirements for $taskTitle"),
            Subtask(title = "Gather necessary materials or information"),
            Subtask(title = "Start with the first small step"),
            Subtask(title = "Continue working and track progress"),
            Subtask(title = "Review and finalize")
        )
    }
}

interface ApiService {
    @POST("graphql")
    suspend fun query(@Body body: GraphQLRequest): GraphQLResponse
}

data class GraphQLRequest(val query: String)
data class GraphQLResponse(val data: Any?, val errors: List<Any>?)
