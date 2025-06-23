package dev.nanday.nutrients_ai_tracker.data

import dev.nanday.nutrients_ai_tracker.data.local.NutrientsDao
import dev.nanday.nutrients_ai_tracker.data.local.models.DbDailyIntake
import dev.nanday.nutrients_ai_tracker.data.local.models.DbDailyIntakeWithFood
import dev.nanday.nutrients_ai_tracker.data.models.Food
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface DailyIntakeRepository {
    fun getTodayFoodIntake(): Flow<List<DbDailyIntakeWithFood>>
    suspend fun saveTodayFoodIntake(food: Food, quantityGrams: Int)
}

class DailyIntakeRepositoryImpl(
    private val dao: NutrientsDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DailyIntakeRepository {

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * Fetches the complete food intake details for today.
     */
    override fun getTodayFoodIntake(): Flow<List<DbDailyIntakeWithFood>> {
        val currentDateString = getCurrentDate()
        return dao.getDailyIntakesWithFoodForDateFlow(currentDateString)
    }


    override suspend fun saveTodayFoodIntake(food: Food, quantityGrams: Int) = withContext(dispatcher) {
        val dailyIntake = DbDailyIntake(date = getCurrentDate(), foodId = food.id, quantity = quantityGrams.toDouble())
        dao.insertDailyIntake(dailyIntake)
    }

    private fun getCurrentDate(): String = sdf.format(Date())
}