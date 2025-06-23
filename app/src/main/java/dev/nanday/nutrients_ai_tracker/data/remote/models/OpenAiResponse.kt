package dev.nanday.nutrients_ai_tracker.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class OpenAiResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: MessageContent
)

@Serializable
data class MessageContent(
    val content: String
)
