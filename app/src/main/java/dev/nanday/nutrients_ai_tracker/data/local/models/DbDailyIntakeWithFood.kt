package dev.nanday.nutrients_ai_tracker.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import dev.nanday.nutrients_ai_tracker.data.local.AppDatabase


data class DbDailyIntakeWithFood(
    // Fields from DbDailyIntake that you want to keep at the top level
    @ColumnInfo(name = AppDatabase.Companion.DAILY_INTAKE_ID)
    val dailyIntakeId: Long,

    @ColumnInfo(name = AppDatabase.Companion.DAILY_INTAKE_DATE)
    val dateString: String,

    @ColumnInfo(name = AppDatabase.Companion.DAILY_INTAKE_QUANTITY)
    val quantity: Double,

    @Embedded
    val food: DbFood
)