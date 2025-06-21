package dev.nanday.nutrients_ai_tracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.nanday.nutrients_ai_tracker.data.local.models.DbDailyIntakeWithFood
import dev.nanday.nutrients_ai_tracker.data.local.models.DbFood
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Dao
interface NutrientsDao {

    @Query("SELECT * FROM ${AppDatabase.FOOD_TABLE_NAME}")
    fun getAllFoods(): Flow<List<DbFood>>

    /**
     * Retrieves a Flow of DailyIntakeWithFood records for the current day.
     * Each record combines DbDailyIntake information with the associated DbFood entity.
     * The date is formatted as "YYYY-MM-dd".
     */
    @Query(
        """
        SELECT
            di.${AppDatabase.DAILY_INTAKE_ID},
            di.${AppDatabase.DAILY_INTAKE_DATE},
            di.${AppDatabase.DAILY_INTAKE_QUANTITY},
            f.*
        FROM ${AppDatabase.DAILY_INTAKE_TABLE_NAME} AS di
        LEFT JOIN ${AppDatabase.FOOD_TABLE_NAME} AS f ON di.food_id = f.${AppDatabase.FOOD_ID}
        WHERE di.${AppDatabase.DAILY_INTAKE_DATE} = :dateString
    """
    )
    fun getDailyIntakesWithFoodForDateFlow(dateString: String): Flow<List<DbDailyIntakeWithFood>>

    /**
     * Convenience function to get daily intakes with food for the current day.
     */
    fun getTodaysDailyIntakesWithFoodFlow(): Flow<List<DbDailyIntakeWithFood>> {
        val currentDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return getDailyIntakesWithFoodForDateFlow(currentDateString)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: DbFood)

    @Query("SELECT * FROM ${AppDatabase.FOOD_TABLE_NAME} WHERE ${AppDatabase.FOOD_ID} = :foodId")
    suspend fun getFoodById(foodId: Long): DbFood?

    @Query("SELECT * FROM ${AppDatabase.FOOD_TABLE_NAME} WHERE ${AppDatabase.FOOD_NAME} LIKE '%' || :query || '%'")
    fun searchFoodByName(query: String): Flow<List<DbFood>>
}