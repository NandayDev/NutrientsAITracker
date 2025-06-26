package dev.nanday.nutrients_ai_tracker.ui.food_list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nanday.nutrients_ai_tracker.data.FoodRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FoodListViewModel @Inject constructor(
    private val foodRepository: FoodRepository
) : ViewModel() {
    val allFoods = foodRepository.getAllFoods().map { list ->
        list.sortedBy { food ->
            food.name
        }
    }
}