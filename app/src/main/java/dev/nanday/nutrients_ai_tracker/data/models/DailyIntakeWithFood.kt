package dev.nanday.nutrients_ai_tracker.data.models

data class DailyIntakeWithFood(
    val id: Long = 0L,
    val date: String,
    val food: Food,
    val quantity: Double
) {
    fun getNutrientQuantity(nutrient: Nutrient): Double {
        return quantity / 100.0 * when (nutrient) {
            Nutrient.CALORIES -> food.energyKcal
            Nutrient.PROTEINS -> food.protein
            Nutrient.FAT -> food.fat
            Nutrient.SATURATED_FAT -> food.saturatedFat
            Nutrient.CARBOHYDRATES -> food.carbohydrates
            Nutrient.SUGAR -> food.sugars
            Nutrient.FIBERS -> food.fiber
            Nutrient.SALT -> food.salt
        }
    }
}