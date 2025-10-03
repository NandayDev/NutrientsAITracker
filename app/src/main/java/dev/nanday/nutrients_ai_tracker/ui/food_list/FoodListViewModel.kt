package dev.nanday.nutrients_ai_tracker.ui.food_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nanday.nutrients_ai_tracker.data.FoodRepository
import dev.nanday.nutrients_ai_tracker.data.models.Food
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FoodListViewModel @Inject constructor(
    foodRepository: FoodRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val foods: StateFlow<List<Food>> = searchQuery
        .combine(foodRepository.getAllFoods()) { query, allFoods ->
            if (query.isBlank()) {
                allFoods.sortedBy { it.name }
            } else {
                allFoods.filter {
                    it.name.contains(query, ignoreCase = true)
                }.sortedBy { it.name }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
