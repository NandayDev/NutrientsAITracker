package dev.nanday.nutrients_ai_tracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nanday.nutrients_ai_tracker.data.DailyIntakeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dailyIntakeRepository: DailyIntakeRepository
) : ViewModel() {
    val foods = dailyIntakeRepository.getTodayFoodIntake().map { list ->
        list.map { dailyIntakeWithFood ->
            DailyIntakeUiModel(name = dailyIntakeWithFood.food.name, quantity = dailyIntakeWithFood.quantity.toString())
        }
    }.stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = listOf())
}