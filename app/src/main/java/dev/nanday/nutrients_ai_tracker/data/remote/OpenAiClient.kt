package dev.nanday.nutrients_ai_tracker.data.remote

import dev.nanday.nutrients_ai_tracker.data.remote.models.OpenAiRequest
import dev.nanday.nutrients_ai_tracker.data.remote.models.OpenAiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAiClient {
    @POST("v1/chat/completions")
    suspend fun analyzeNutrition(
        @Header("Authorization") authHeader: String,
        @Body request: OpenAiRequest
    ): Response<OpenAiResponse>
}
