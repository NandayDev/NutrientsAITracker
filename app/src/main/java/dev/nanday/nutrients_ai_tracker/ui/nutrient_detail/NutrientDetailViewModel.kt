package dev.nanday.nutrients_ai_tracker.ui.nutrient_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nanday.nutrients_ai_tracker.data.DailyIntakeRepository
import dev.nanday.nutrients_ai_tracker.data.models.Nutrient
import dev.nanday.nutrients_ai_tracker.ui.NutrientUiModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NutrientDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    dailyIntakeRepository: DailyIntakeRepository
) : ViewModel() {

    val nutrientNumber: Int = savedStateHandle.get<Int>("nutrientNumber")!!
    val nutrient = Nutrient.fromInt(nutrientNumber)!!

    val nutrientDetailState: StateFlow<NutrientDetailScreenState> = dailyIntakeRepository.foodIntakeFlow.map { dailyIntakes ->
        var totalNutrient = 0.0
        val uiModels = dailyIntakes.map { dailyIntakeWithFood ->
            val nutrientQuantity = dailyIntakeWithFood.getNutrientQuantity(nutrient)
            totalNutrient += nutrientQuantity
            ContributingFoodItemUiModel(
                id = dailyIntakeWithFood.id.toString(),
                name = dailyIntakeWithFood.food.name,
                amountConsumed = "${dailyIntakeWithFood.quantity} g",
                nutrientContribution = nutrientQuantity,
                nutrientUnit = nutrient.unit,
                nutrient = nutrient
            )
        }.sortedByDescending { it.nutrientContribution }

        val nutrientProgress = NutrientUiModel(
            nutrient = nutrient,
            currentValue = totalNutrient,
            targetValue = nutrient.getRecommendedQuantity(),
            unit = nutrient.unit
        )

        NutrientDetailScreenState(nutrient.localizedName, nutrientProgress, uiModels)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NutrientDetailScreenState("", null, emptyList())
    )
}

data class ContributingFoodItemUiModel(
    val id: String,
    val name: String,
    val amountConsumed: String, // Es. "100 g"
    val nutrientContribution: Double,
    val nutrientUnit: String,
    val nutrient: Nutrient
)

data class NutrientDetailScreenState(
    val nutrientName: String?,
    val nutrientProgress: NutrientUiModel?,
    val uiModels: List<ContributingFoodItemUiModel>
)