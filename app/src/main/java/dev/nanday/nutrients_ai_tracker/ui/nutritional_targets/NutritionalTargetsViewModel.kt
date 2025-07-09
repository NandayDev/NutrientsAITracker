package dev.nanday.nutrients_ai_tracker.ui.nutritional_targets

import androidx.compose.runtime.asFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nanday.nutrients_ai_tracker.data.NutritionalTargetRepository
import dev.nanday.nutrients_ai_tracker.data.models.NutritionalTargets
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class NutritionalTargetViewModel @Inject constructor(
    private val nutritionalTargetRepository: NutritionalTargetRepository
) : ViewModel() {

    private val _calories = mutableFloatStateOf(2000f)
    val calories: androidx.compose.runtime.State<Float> = _calories

    private val _protein = mutableFloatStateOf(70f)
    val protein = _protein.asFloatState()

    private val _fat = mutableFloatStateOf(70f)
    val fat = _fat.asFloatState()

    private val _saturatedFat = mutableFloatStateOf(20f)
    val saturatedFat = _saturatedFat.asFloatState()

    private val _carbohydrates = mutableFloatStateOf(250f)
    val carbohydrates = _carbohydrates.asFloatState()

    private val _sugars = mutableFloatStateOf(50f)
    val sugars = _sugars.asFloatState()

    private val _fiber = mutableFloatStateOf(30f)
    val fiber = _fiber.asFloatState()

    private val _salt = mutableFloatStateOf(6f)
    val salt = _salt.asFloatState()

    init {
        loadTargets()
    }

    private fun loadTargets() {
        viewModelScope.launch {
            val targets = nutritionalTargetRepository.targetsFlow.value
            _calories.floatValue = targets.calories.toFloat()
            _protein.floatValue = targets.protein.toFloat()
            _fat.floatValue = targets.fat.toFloat()
            _saturatedFat.floatValue = targets.saturatedFat.toFloat()
            _carbohydrates.floatValue = targets.carbohydrates.toFloat()
            _sugars.floatValue = targets.sugars.toFloat()
            _fiber.floatValue = targets.fiber.toFloat()
            _salt.floatValue = targets.salt.toFloat()
        }
    }


    fun onCaloriesChange(newValue: Float) {
        _calories.floatValue = newValue
    }

    fun onProteinChange(newValue: Float) {
        _protein.floatValue = newValue
    }

    fun onFatChange(newValue: Float) {
        _fat.floatValue = newValue
    }

    fun onSaturatedFatChange(newValue: Float) {
        _saturatedFat.floatValue = newValue
    }

    fun onCarbohydratesChange(newValue: Float) {
        _carbohydrates.floatValue = newValue
    }

    fun onSugarsChange(newValue: Float) {
        _sugars.floatValue = newValue
    }

    fun onFiberChange(newValue: Float) {
        _fiber.floatValue = newValue
    }

    fun onSaltChange(newValue: Float) {
        _salt.floatValue = newValue
    }

    fun saveTargets() {
        viewModelScope.launch {
            val currentTargets = NutritionalTargets(
                calories = _calories.floatValue.roundToInt(),
                protein = _protein.floatValue.roundToInt(),
                fat = _fat.floatValue.roundToInt(),
                saturatedFat = _saturatedFat.floatValue.roundToInt(),
                carbohydrates = _carbohydrates.floatValue.roundToInt(),
                sugars = _sugars.floatValue.roundToInt(),
                fiber = _fiber.floatValue.roundToInt(),
                salt = _salt.floatValue.roundToInt()
            )
            nutritionalTargetRepository.saveTargets(currentTargets)
        }
    }
}