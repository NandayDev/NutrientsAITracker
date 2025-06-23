package dev.nanday.nutrients_ai_tracker.ui.add_daily_intake // Crea questo package

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

    private val foodId: Long = savedStateHandle.get<Long>("foodId")!!

    private val _food = MutableStateFlow<Food?>(null)
    val food: StateFlow<Food?> = _food.asStateFlow()

    // Stato per la quantità in grammi inserita dall'utente
    private val _grams = mutableStateOf("") // Stringa per gestire l'input del TextField
    val grams: State<String> = _grams

    // Stato per le informazioni nutrizionali calcolate (per 100g e per la quantità inserita)
    // Dati fittizi per ora per 100g
    private val _baseNutrientsPer100g = MutableStateFlow<NutrientInfo?>(null)
    // val baseNutrientsPer100g: StateFlow<NutrientInfo?> = _baseNutrientsPer100g.asStateFlow() // Non esposta direttamente

    private val _calculatedNutrients = MutableStateFlow<NutrientInfo?>(null)
    val calculatedNutrients: StateFlow<NutrientInfo?> = _calculatedNutrients.asStateFlow()

    init {
        if (foodId != -1L) {
            viewModelScope.launch {
                _food.value = foodRepository.getFoodById(foodId) // Assicurati che il repository abbia questo metodo
                _food.value?.let {
                    _baseNutrientsPer100g.value = fetchBaseNutrientsForFood(it) // Funzione fittizia
                    updateCalculatedNutrients() // Calcola inizialmente per 0 grammi o per i grammi salvati se ci sono
                }
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
            dailyIntakeRepository.saveTodayFoodIntake(currentFood, currentGrams)
        } else {
            println("Errore: Dati non validi per il salvataggio.")
        }
    }
}