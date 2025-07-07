package dev.nanday.nutrients_ai_tracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.nanday.nutrients_ai_tracker.data.local.models.DbDailyIntake
import dev.nanday.nutrients_ai_tracker.data.local.models.DbDailyIntakeWithFood
import dev.nanday.nutrients_ai_tracker.data.local.models.DbFood
import kotlinx.coroutines.flow.Flow

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyIntake(dbDailyIntake: DbDailyIntake)

    /**
     * Retrieves a specific DbDailyIntake by its ID.
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
        WHERE di.${AppDatabase.DAILY_INTAKE_ID} = :id
    """
    )
    suspend fun getDailyIntakeWithFoodById(id: Long): DbDailyIntakeWithFood?

    /**
     * Deletes a specific DbDailyIntake from the database.
     */
    @Delete
    suspend fun deleteDailyIntake(dbDailyIntake: DbDailyIntake)

    @Insert
    suspend fun insertFood(food: DbFood)

    @Update
    suspend fun updateFood(food: DbFood)

    @Delete
    suspend fun deleteFood(food: DbFood)

    @Query("SELECT * FROM ${AppDatabase.FOOD_TABLE_NAME} WHERE ${AppDatabase.FOOD_ID} = :foodId")
    suspend fun getFoodById(foodId: Long): DbFood?

    @Query("SELECT * FROM ${AppDatabase.FOOD_TABLE_NAME} WHERE ${AppDatabase.FOOD_NAME} LIKE '%' || :query || '%'")
    fun searchFoodByName(query: String): Flow<List<DbFood>>
}