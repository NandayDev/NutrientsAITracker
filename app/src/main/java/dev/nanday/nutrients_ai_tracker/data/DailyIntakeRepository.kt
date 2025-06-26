@file:OptIn(ExperimentalCoroutinesApi::class)

package dev.nanday.nutrients_ai_tracker.data

import android.annotation.SuppressLint
import dev.nanday.nutrients_ai_tracker.data.local.NutrientsDao
import dev.nanday.nutrients_ai_tracker.data.local.models.DbDailyIntake
import dev.nanday.nutrients_ai_tracker.data.models.DailyIntakeWithFood
import dev.nanday.nutrients_ai_tracker.data.models.Food
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface DailyIntakeRepository {
    val currentDateFlow: MutableStateFlow<Date>
    val foodIntakeFlow: Flow<List<DailyIntakeWithFood>>
    suspend fun getDailyIntakeWithFoodById(id: Long): DailyIntakeWithFood?
    suspend fun createDailyIntake(food: Food, quantityGrams: Int)
    suspend fun updateDailyIntake(dailyIntakeId: Long, food: Food, quantityGrams: Int)
    suspend fun deleteDailyIntake(dailyIntakeId: Long)
}

class DailyIntakeRepositoryImpl(
    private val dao: NutrientsDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : DailyIntakeRepository {

    override val currentDateFlow = MutableStateFlow(Date())

    /**
     * Fetches the complete food intake details for today.
     */
    override val foodIntakeFlow: Flow<List<DailyIntakeWithFood>> =
        currentDateFlow.flatMapLatest { date ->
            val currentDateString = sdf.format(date)
            dao.getDailyIntakesWithFoodForDateFlow(currentDateString).map {
                it.map {
                    it.toDailyIntakeWithFood()
                }
            }
        }

    override suspend fun getDailyIntakeWithFoodById(id: Long): DailyIntakeWithFood? = withContext(dispatcher) {
        dao.getDailyIntakeWithFoodById(id)?.toDailyIntakeWithFood()
    }

    override suspend fun createDailyIntake(food: Food, quantityGrams: Int) = withContext(dispatcher) {
        val dailyIntake = DbDailyIntake(date = getCurrentDateString(currentDateFlow.value), foodId = food.id, quantity = quantityGrams.toDouble())
        dao.insertDailyIntake(dailyIntake)
    }

    override suspend fun updateDailyIntake(dailyIntakeId: Long, food: Food, quantityGrams: Int) = withContext(dispatcher) {
        val dailyIntake = DbDailyIntake(id = dailyIntakeId, date = getCurrentDateString(currentDateFlow.value), foodId = food.id, quantity = quantityGrams.toDouble())
        dao.insertDailyIntake(dailyIntake)
    }

    override suspend fun deleteDailyIntake(dailyIntakeId: Long) = withContext(dispatcher) {
        dao.deleteDailyIntake(
            DbDailyIntake(id = dailyIntakeId, date = "", foodId = 0, quantity = 0.0)
        )
    }

    companion object {
        @SuppressLint("ConstantLocale")
        private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        private fun getCurrentDateString(date: Date): String = sdf.format(date)
    }
}