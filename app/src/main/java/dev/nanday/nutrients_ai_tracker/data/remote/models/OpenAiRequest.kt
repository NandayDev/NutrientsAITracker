package dev.nanday.nutrients_ai_tracker.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class OpenAiRequest(
    val model: String,
    val messages: List<Message>,
    val max_tokens: Int
)

@Serializable
data class Message(
    val role: String,
    val content: List<Content>
)

interface Content

@Serializable
data class ContentImage(
    val type: String,
    val image_url: ImageUrl
) : Content

@Serializable
data class ContentText(
    val type: String,
    val text: String
) : Content

@Serializable
data class ImageUrl(
    val url: String
)
