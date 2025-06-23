package dev.nanday.nutrients_ai_tracker.data.models

data class Food(
    val id: Long = 0L,
    val name: String,

    val photo: ByteArray?,

    val energyKj: Double,        // energia in kJ per 100g
    val energyKcal: Double,      // energia in kcal per 100g

    val fat: Double,             // grassi totali in grammi per 100g
    val saturatedFat: Double,    // grassi saturi in grammi per 100g

    val carbohydrates: Double,   // carboidrati totali in grammi per 100g
    val sugars: Double,          // zuccheri in grammi per 100g

    val fiber: Double,           // fibre in grammi per 100g
    val protein: Double,         // proteine in grammi per 100g

    val salt: Double             // sale in grammi per 100g
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Food
        if (id != other.id) return false
        if (name != other.name) return false
        if (photo != null) {
            if (other.photo == null) return false
            if (!photo.contentEquals(other.photo)) return false
        } else if (other.photo != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + energyKj.hashCode()
        result = 31 * result + energyKcal.hashCode()
        result = 31 * result + fat.hashCode()
        result = 31 * result + saturatedFat.hashCode()
        result = 31 * result + carbohydrates.hashCode()
        result = 31 * result + sugars.hashCode()
        result = 31 * result + fiber.hashCode()
        result = 31 * result + protein.hashCode()
        result = 31 * result + salt.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (photo?.contentHashCode() ?: 0)
        return result
    }


}