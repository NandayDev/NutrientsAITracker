package dev.nanday.nutrients_ai_tracker.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.nanday.nutrients_ai_tracker.data.local.AppDatabase
import dev.nanday.nutrients_ai_tracker.data.models.Food

@Entity(tableName = AppDatabase.Companion.FOOD_TABLE_NAME)
data class DbFood(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = AppDatabase.Companion.FOOD_ID)
    val id: Long = 0L,
    @ColumnInfo(name = AppDatabase.Companion.FOOD_NAME)
    val name: String,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) // Store as BLOB for byte array
    val photo: ByteArray? = null,

    @ColumnInfo(name = AppDatabase.Companion.FOOD_ENERGY_KJ)
    val energyKj: Double,
    @ColumnInfo(name = AppDatabase.Companion.FOOD_ENERGY_KCAL)
    val energyKcal: Double,

    @ColumnInfo(name = AppDatabase.Companion.FOOD_FAT)
    val fat: Double,
    @ColumnInfo(name = AppDatabase.Companion.FOOD_SATURATED_FAT)
    val saturatedFat: Double,

    @ColumnInfo(name = AppDatabase.Companion.FOOD_CARBOHYDRATES)
    val carbohydrates: Double,

    @ColumnInfo(name = AppDatabase.Companion.FOOD_SUGAR)
    val sugars: Double,

    @ColumnInfo(name = AppDatabase.Companion.FOOD_FIBERS)
    val fiber: Double,
    @ColumnInfo(name = AppDatabase.Companion.FOOD_PROTEINS)
    val protein: Double,

    @ColumnInfo(name = AppDatabase.Companion.FOOD_SALT)
    val salt: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DbFood

        if (id != other.id) return false
        if (energyKj != other.energyKj) return false
        if (energyKcal != other.energyKcal) return false
        if (fat != other.fat) return false
        if (saturatedFat != other.saturatedFat) return false
        if (carbohydrates != other.carbohydrates) return false
        if (sugars != other.sugars) return false
        if (fiber != other.fiber) return false
        if (protein != other.protein) return false
        if (salt != other.salt) return false
        if (name != other.name) return false
        if (!photo.contentEquals(other.photo)) return false

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

    fun toFood() = Food(
        id = id,
        name = name,
        photo = photo,
        energyKj = energyKj,
        energyKcal = energyKcal,
        fat = fat,
        saturatedFat = saturatedFat,
        carbohydrates = carbohydrates,
        sugars = sugars,
        fiber = fiber,
        protein = protein,
        salt = salt
    )

    companion object {
        fun fromFood(food: Food) = DbFood(
            id = food.id,
            name = food.name,
            photo = food.photo,
            energyKj = food.energyKj,
            energyKcal = food.energyKcal,
            fat = food.fat,
            saturatedFat = food.saturatedFat,
            carbohydrates = food.carbohydrates,
            sugars = food.sugars,
            fiber = food.fiber,
            protein = food.protein,
            salt = food.salt
        )
    }
}