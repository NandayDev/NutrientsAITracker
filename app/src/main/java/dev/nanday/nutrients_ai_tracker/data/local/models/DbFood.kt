package dev.nanday.nutrients_ai_tracker.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.nanday.nutrients_ai_tracker.data.local.AppDatabase

@Entity(tableName = AppDatabase.Companion.FOOD_TABLE_NAME)
data class DbFood(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = AppDatabase.Companion.FOOD_ID)
    val id: Long = 0L,
    @ColumnInfo(name = AppDatabase.Companion.FOOD_NAME)
    val name: String,

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
)