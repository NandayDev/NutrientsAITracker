package dev.nanday.nutrients_ai_tracker.data

import com.google.gson.Gson
import dev.nanday.nutrients_ai_tracker.BuildConfig
import dev.nanday.nutrients_ai_tracker.data.remote.OpenAiClient
import dev.nanday.nutrients_ai_tracker.data.remote.models.ContentImage
import dev.nanday.nutrients_ai_tracker.data.remote.models.ContentText
import dev.nanday.nutrients_ai_tracker.data.remote.models.ImageUrl
import dev.nanday.nutrients_ai_tracker.data.remote.models.Message
import dev.nanday.nutrients_ai_tracker.data.remote.models.OpenAiFoodResponse
import dev.nanday.nutrients_ai_tracker.data.remote.models.OpenAiRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface OpenAiRepository {
    suspend fun getFoodFromImage(imageBase64: String): OpenAiFoodResponse?
}

class OpenAiRepositoryImpl @Inject constructor(
    private val client: OpenAiClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : OpenAiRepository {
    override suspend fun getFoodFromImage(imageBase64: String): OpenAiFoodResponse? = withContext(dispatcher) {
        val prompt = """
        Estrai i seguenti dati nutrizionali per 100g dalla tabella nell’immagine. Restituisci **solo** un JSON con questi campi:
        {
          "energyKj": Double,
          "energyKcal": Double,
          "fat": Double,
          "saturatedFat": Double,
          "carbohydrates": Double,
          "sugars": Double,
          "fiber": Double,
          "protein": Double,
          "salt": Double
        }
    """.trimIndent()

        val response = client.analyzeNutrition(
            authHeader = "Bearer ${BuildConfig.OPENAI_API_KEY}",
            request = OpenAiRequest(
                model = "gpt-4o",
                messages = listOf(
                    Message(
                        role = "user",
                        content = listOf(
                            ContentImage(
                                type = "image_url",
                                image_url = ImageUrl("data:image/jpeg;base64,$imageBase64")
                            ),
                            ContentText(
                                type = "text",
                                text = prompt
                            )
                        )
                    )
                ),
                max_tokens = 1000
            )
        )

        if (response.isSuccessful) {
            val jsonContent = response.body()?.choices?.firstOrNull()?.message?.content?.replace("```json", "")?.replace("```", "")
            jsonContent?.let {
                return@withContext try {
                    Gson().fromJson(it, OpenAiFoodResponse::class.java)
                } catch (_: Exception) {
                    null
                }
            }
        }
        null
    }

}