package dev.nanday.nutrients_ai_tracker.data.models

enum class Nutrient(val number: Int, val unit: String, val localizedName: String) {
    CALORIES(0, "kcal", "Calorie"),
    PROTEINS(1, "g", "Proteine"),
    FAT(2, "g", "Grassi"),
    SATURATED_FAT(3, "g", "Grassi saturi"),
    CARBOHYDRATES(4, "g", "Carboidrati"),
    SUGAR(5, "g", "Zuccheri"),
    FIBERS(6, "g", "Fibre"),
    SALT(7, "g", "Sale");

    companion object {
        private val map by lazy { entries.associateBy { it.number } }
        fun fromInt(number: Int) = map[number]
    }
}