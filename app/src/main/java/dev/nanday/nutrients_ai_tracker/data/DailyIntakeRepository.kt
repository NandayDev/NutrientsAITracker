package dev.nanday.nutrients_ai_tracker.data

import dev.nanday.nutrients_ai_tracker.data.local.NutrientsDao
import dev.nanday.nutrients_ai_tracker.data.local.models.DbDailyIntakeWithFood
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface DailyIntakeRepository {
    fun getTodayFoodIntake(): Flow<List<DbDailyIntakeWithFood>>
}

class DailyIntakeRepositoryImpl(
    private val dao: NutrientsDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DailyIntakeRepository {

    /**
     * Fetches the complete food intake details for today.
     */
    override fun getTodayFoodIntake(): Flow<List<DbDailyIntakeWithFood>> {
        return dao.getTodaysDailyIntakesWithFoodFlow()
    }
}