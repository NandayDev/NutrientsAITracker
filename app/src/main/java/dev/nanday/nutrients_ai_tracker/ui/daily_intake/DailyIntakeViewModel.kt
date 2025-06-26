package dev.nanday.nutrients_ai_tracker.ui.daily_intake // Crea questo package

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nanday.nutrients_ai_tracker.data.DailyIntakeRepository
import dev.nanday.nutrients_ai_tracker.data.FoodRepository
import dev.nanday.nutrients_ai_tracker.data.models.Food
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

data class NutrientInfo(
    val energyKcal: Double,
    val protein: Double,
    val fat: Double,
    val saturatedFat: Double,
    val carbohydrates: Double,
    val sugars: Double,
    val fiber: Double,
    val salt: Double
) {
    companion object {
        val Empty = NutrientInfo(energyKcal = 0.0, protein = 0.0, fat = 0.0, saturatedFat = 0.0, carbohydrates = 0.0, sugars = 0.0, fiber = 0.0, salt = 0.0)
    }
}

@HiltViewModel
class AddDailyIntakeViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val dailyIntakeRepository: DailyIntakeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val foodId = savedStateHandle.get<Long>("foodId")
    private val dailyIntakeId = savedStateHandle.get<Long>("dailyIntakeId")

    private val _food = MutableStateFlow<Food?>(null)
    val food: StateFlow<Food?> = _food.asStateFlow()

    private val _grams = mutableStateOf("")
    val grams: State<String> = _grams

    private val _baseNutrientsPer100g = MutableStateFlow<NutrientInfo?>(null)

    private val _calculatedNutrients = MutableStateFlow<NutrientInfo?>(null)
    val calculatedNutrients: StateFlow<NutrientInfo?> = _calculatedNutrients.asStateFlow()

    val _shouldShowDeleteButton = MutableStateFlow(false)
    val shouldShowDeleteButton = _shouldShowDeleteButton.asStateFlow()

    init {
        viewModelScope.launch {
            _food.value = if (foodId != null) {
                _shouldShowDeleteButton.value = false
                foodRepository.getFoodById(foodId) // Assicurati che il repository abbia questo metodo
            } else if (dailyIntakeId != null) {
                val dailyIntake = dailyIntakeRepository.getDailyIntakeWithFoodById(dailyIntakeId)
                _grams.value = dailyIntake?.quantity?.roundToInt().toString()
                _shouldShowDeleteButton.value = true
                dailyIntake?.food
            } else {
                return@launch
            }

            _food.value?.let {
                _baseNutrientsPer100g.value = fetchBaseNutrientsForFood(it) // Funzione fittizia
                updateCalculatedNutrients() // Calcola inizialmente per 0 grammi o per i grammi salvati se ci sono
            }
        }
    }

    private fun fetchBaseNutrientsForFood(food: Food): NutrientInfo {
        return NutrientInfo(
            energyKcal = food.energyKcal,
            protein = food.protein,
            fat = food.fat,
            saturatedFat = food.saturatedFat,
            carbohydrates = food.carbohydrates,
            sugars = food.sugars,
            fiber = food.fiber,
            salt = food.salt
        )
    }

    fun onGramsChanged(newGrams: String) {
        // Permetti solo cifre e un input ragionevole
        if (newGrams.all { it.isDigit() } && newGrams.length <= 5) { // Limita la lunghezza
            _grams.value = newGrams
            updateCalculatedNutrients()
        } else if (newGrams.isEmpty()) {
            _grams.value = ""
            updateCalculatedNutrients()
        }
    }

    private fun updateCalculatedNutrients() {
        val currentGrams = _grams.value.toDoubleOrNull() ?: 0.0
        val base = _baseNutrientsPer100g.value

        if (base != null) {
            val factor = currentGrams / 100.0
            _calculatedNutrients.value = NutrientInfo(
                energyKcal = base.energyKcal * factor,
                protein = base.protein * factor,
                fat = base.fat * factor,
                saturatedFat = base.saturatedFat * factor,
                carbohydrates = base.carbohydrates * factor,
                sugars = base.sugars * factor,
                fiber = base.fiber * factor,
                salt = base.salt * factor
            )
        } else {
            _calculatedNutrients.value = NutrientInfo.Empty
        }
    }

    suspend fun onSaveClicked() {
        val currentFood = _food.value
        val currentGrams = _grams.value.toIntOrNull()
        val nutrients = _calculatedNutrients.value

        if (currentFood != null && currentGrams != null && currentGrams > 0 && nutrients != null) {
            if (dailyIntakeId == null) {
                dailyIntakeRepository.createDailyIntake(currentFood, currentGrams)
            } else {
                dailyIntakeRepository.updateDailyIntake(
                    dailyIntakeId = dailyIntakeId,
                    food = currentFood,
                    quantityGrams = currentGrams
                )
            }
        }
    }

    suspend fun delete() {
        dailyIntakeRepository.deleteDailyIntake(dailyIntakeId!!)
    }
}