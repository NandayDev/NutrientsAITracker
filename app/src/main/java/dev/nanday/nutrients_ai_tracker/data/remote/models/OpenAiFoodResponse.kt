package dev.nanday.nutrients_ai_tracker.data.remote.models

data class OpenAiFoodResponse(
    val energyKj: Double,        // energia in kJ per 100g
    val energyKcal: Double,      // energia in kcal per 100g

    val fat: Double,             // grassi totali in grammi per 100g
    val saturatedFat: Double,    // grassi saturi in grammi per 100g

    val carbohydrates: Double,   // carboidrati totali in grammi per 100g
    val sugars: Double,          // zuccheri in grammi per 100g

    val fiber: Double,           // fibre in grammi per 100g
    val protein: Double,         // proteine in grammi per 100g

    val salt: Double
)