package dev.nanday.nutrients_ai_tracker.data

import dev.nanday.nutrients_ai_tracker.data.local.NutrientsDao
import dev.nanday.nutrients_ai_tracker.data.local.models.DbFood
import dev.nanday.nutrients_ai_tracker.data.models.Food
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface FoodRepository {
    suspend fun insertFood(food: Food)
    suspend fun updateFood(food: Food)
    suspend fun deleteFood(food: Food)
    suspend fun getFoodById(id: Long): Food?
    fun getAllFoods(): Flow<List<Food>>
}

class FoodRepositoryImpl @Inject constructor(
    private val dao: NutrientsDao,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : FoodRepository {
    override suspend fun insertFood(food: Food) = withContext(dispatcher) {
        val dbFood = DbFood.fromFood(food)
        dao.insertFood(dbFood)
    }

    override suspend fun updateFood(food: Food) = withContext(dispatcher) {
        val dbFood = DbFood.fromFood(food)
        dao.insertFood(dbFood)
    }

    override suspend fun deleteFood(food: Food) {
        val dbFood = DbFood.fromFood(food)
        dao.deleteFood(dbFood)
    }

    override suspend fun getFoodById(id: Long): Food? = withContext(dispatcher) {
        dao.getFoodById(id)?.toFood()
    }

    override fun getAllFoods() = dao.getAllFoods().map { list ->
        list.map { dbFood ->
            dbFood.toFood()
        }
    }
}