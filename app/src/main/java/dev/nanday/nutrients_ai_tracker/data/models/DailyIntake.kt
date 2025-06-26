package dev.nanday.nutrients_ai_tracker.data.models

data class DailyIntake(
    val id: Long = 0L,
    val date: String,
    val food: Food,
    val quantity: Double
)