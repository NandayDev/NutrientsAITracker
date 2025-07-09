package dev.nanday.nutrients_ai_tracker.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.RESTRICT
import androidx.room.PrimaryKey
import dev.nanday.nutrients_ai_tracker.data.local.AppDatabase
import dev.nanday.nutrients_ai_tracker.data.models.DailyIntake

@Entity(
    tableName = AppDatabase.Companion.DAILY_INTAKE_TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = DbFood::class,
            parentColumns = [AppDatabase.Companion.FOOD_ID],
            childColumns = [AppDatabase.Companion.DAILY_INTAKE_FOOD_ID],
            onDelete = RESTRICT
        )
    ]
)
data class DbDailyIntake(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = AppDatabase.Companion.DAILY_INTAKE_ID)
    val id: Long = 0L,

    @ColumnInfo(name = AppDatabase.Companion.DAILY_INTAKE_DATE)
    val date: String,

    @ColumnInfo(name = AppDatabase.Companion.DAILY_INTAKE_FOOD_ID)
    val foodId: Long,

    @ColumnInfo(name = AppDatabase.Companion.DAILY_INTAKE_QUANTITY)
    val quantity: Double
) {
    fun toDailyIntake() = DailyIntake(
        id = id,
        date = date,
        foodId = foodId,
        quantity = quantity
    )
}