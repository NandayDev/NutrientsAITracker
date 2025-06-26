package dev.nanday.nutrients_ai_tracker.data.models

import dev.nanday.nutrients_ai_tracker.ui.HomeViewModel.RecommendedQuantities

enum class Nutrient(val number: Int, val unit: String, val localizedName: String) {
    CALORIES(0, "kcal", "Calorie"),
    PROTEINS(1, "g", "Proteine"),
    FAT(2, "g", "Grassi"),
    SATURATED_FAT(3, "g", "Grassi saturi"),
    CARBOHYDRATES(4, "g", "Carboidrati"),
    SUGAR(5, "g", "Zuccheri"),
    FIBERS(6, "g", "Fibre"),
    SALT(7, "g", "Sale");

    fun getRecommendedQuantity(): Double {
        return when (this) {
            CALORIES -> RecommendedQuantities.ENERGY_KCAL
            PROTEINS -> RecommendedQuantities.PROTEINS
            FAT -> RecommendedQuantities.FAT
            SATURATED_FAT -> RecommendedQuantities.SATURATED_FAT
            CARBOHYDRATES -> RecommendedQuantities.CARBOHYDRATES
            SUGAR -> RecommendedQuantities.SUGAR
            FIBERS -> RecommendedQuantities.FIBERS
            SALT -> RecommendedQuantities.SALT
        }
    }

    companion object {
        private val map by lazy { entries.associateBy { it.number } }
        fun fromInt(number: Int) = map[number]
    }
}