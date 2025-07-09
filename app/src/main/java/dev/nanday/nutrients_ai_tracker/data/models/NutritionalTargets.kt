package dev.nanday.nutrients_ai_tracker.data.models

data class NutritionalTargets(
    val calories: Int,
    val protein: Int,
    val fat: Int,
    val saturatedFat: Int,
    val carbohydrates: Int,
    val sugars: Int,
    val fiber: Int,
    val salt: Int
)