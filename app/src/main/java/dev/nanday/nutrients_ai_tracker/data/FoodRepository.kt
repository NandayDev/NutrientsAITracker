package dev.nanday.nutrients_ai_tracker.data

import dev.nanday.nutrients_ai_tracker.data.local.NutrientsDao
import dev.nanday.nutrients_ai_tracker.data.local.models.DbFood
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface FoodRepository {
    fun getAllFoods(): Flow<List<DbFood>>
}

class FoodRepositoryImpl @Inject constructor(
    private val dao: NutrientsDao
) : FoodRepository {
    override fun getAllFoods() = dao.getAllFoods()
}