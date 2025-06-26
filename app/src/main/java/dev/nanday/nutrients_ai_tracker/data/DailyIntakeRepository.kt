@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.nanday.nutrients_ai_tracker.data

import android.annotation.SuppressLint
import dev.nanday.nutrients_ai_tracker.data.local.NutrientsDao
import dev.nanday.nutrients_ai_tracker.data.local.models.DbDailyIntake
import dev.nanday.nutrients_ai_tracker.data.local.models.DbDailyIntakeWithFood
import dev.nanday.nutrients_ai_tracker.data.models.Food
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface DailyIntakeRepository {
    val currentDateFlow: MutableStateFlow<Date>
    val foodIntakeFlow: Flow<List<DbDailyIntakeWithFood>>
    suspend fun saveTodayFoodIntake(food: Food, quantityGrams: Int)
}

class DailyIntakeRepositoryImpl(
    private val dao: NutrientsDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DailyIntakeRepository {

    override val currentDateFlow = MutableStateFlow(Date())

    /**
     * Fetches the complete food intake details for today.
     */
    override val foodIntakeFlow: Flow<List<DbDailyIntakeWithFood>> =
        currentDateFlow.flatMapLatest { date ->
            val currentDateString = sdf.format(date)
            dao.getDailyIntakesWithFoodForDateFlow(currentDateString)
        }

    override suspend fun saveTodayFoodIntake(food: Food, quantityGrams: Int) = withContext(dispatcher) {
        val dailyIntake = DbDailyIntake(date = getCurrentDateString(currentDateFlow.value), foodId = food.id, quantity = quantityGrams.toDouble())
        dao.insertDailyIntake(dailyIntake)
    }

    companion object {
        @SuppressLint("ConstantLocale")
        private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        private fun getCurrentDateString(date: Date): String = sdf.format(date)
    }
}