package dev.nanday.nutrients_ai_tracker.ui.date_picker

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nanday.nutrients_ai_tracker.data.DailyIntakeRepository
import javax.inject.Inject

@HiltViewModel
class DateChoiceViewModel @Inject constructor(
    private val dailyIntakeRepository: DailyIntakeRepository
) : ViewModel() {
    var selectedDate = dailyIntakeRepository.currentDateFlow.value
        set(value) {
            field = value
            dailyIntakeRepository.currentDateFlow.value = value
        }
}