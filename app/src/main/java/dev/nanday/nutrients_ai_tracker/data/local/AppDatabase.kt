package dev.nanday.nutrients_ai_tracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.nanday.nutrients_ai_tracker.data.local.models.DbDailyIntake
import dev.nanday.nutrients_ai_tracker.data.local.models.DbFood

@Database(entities = [DbFood::class, DbDailyIntake::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): NutrientsDao

    companion object {
        const val FOOD_TABLE_NAME = "foods"
        const val FOOD_ID = "fd_id"
        const val FOOD_NAME = "name"
        const val FOOD_ENERGY_KJ = "energy_kj"
        const val FOOD_ENERGY_KCAL = "energy_kcal"
        const val FOOD_FAT = "fat"
        const val FOOD_SATURATED_FAT = "saturated_fat"
        const val FOOD_CARBOHYDRATES = "carbohydrates"
        const val FOOD_SUGAR = "sugar"
        const val FOOD_FIBERS = "fibers"
        const val FOOD_PROTEINS = "proteins"
        const val FOOD_SALT = "salt"

        const val DAILY_INTAKE_TABLE_NAME = "daily_intakes"
        const val DAILY_INTAKE_ID = "di_id"
        const val DAILY_INTAKE_FOOD_ID = "food_id"
        const val DAILY_INTAKE_DATE = "date"
        const val DAILY_INTAKE_QUANTITY = "quantity"
    }
}