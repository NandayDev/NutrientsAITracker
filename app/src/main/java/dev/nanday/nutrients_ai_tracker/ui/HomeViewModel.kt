package dev.nanday.nutrients_ai_tracker.ui // o il tuo package corretto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.nanday.nutrients_ai_tracker.data.DailyIntakeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


// Modello UI per i totali dei nutrienti
data class NutrientTotalsUiModel(
    val totalEnergyKcal: NutrientUiModel,
    val totalProteinGrams: NutrientUiModel,
    val totalFatGrams: NutrientUiModel,
    val totalSaturatedFatGrams: NutrientUiModel,
    val totalCarbsGrams: NutrientUiModel,
    val totalFiberGrams: NutrientUiModel,
    val totalSugarGrams: NutrientUiModel,
    val totalSaltGrams: NutrientUiModel
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dailyIntakeRepository: DailyIntakeRepository
) : ViewModel() {

    // Flusso originale dei dati dal repository
    private val todayIntakeFlow = dailyIntakeRepository.getTodayFoodIntake()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // O Eagerly se preferisci
            initialValue = emptyList()
        )

    // Modelli UI per la lista degli alimenti consumati
    val consumedFoodsUi: StateFlow<List<DailyIntakeUiModel>> = todayIntakeFlow
        .map { list ->
            list.map { dailyIntakeWithFood ->
                DailyIntakeUiModel(
                    id = dailyIntakeWithFood.dailyIntakeId,
                    name = dailyIntakeWithFood.food.name,
                    quantity = "${dailyIntakeWithFood.quantity} g"
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Modelli UI per i totali dei nutrienti
    val nutrientTotalsUi: StateFlow<NutrientTotalsUiModel?> = todayIntakeFlow
        .map { list ->
            var totalEnergy = 0.0
            var totalProtein = 0.0
            var totalFat = 0.0
            var totalCarbs = 0.0
            var totalFiber = 0.0
            var totalSugar = 0.0
            var totalSalt = 0.0
            var totalSaturatedFat = 0.0

            list.forEach { dailyIntakeWithFood ->
                // Assumiamo che il tuo modello 'Food' abbia questi campi nutrizionali per 100g
                // e 'dailyIntake.quantityGrams' sia la quantità consumata in grammi.
                val food = dailyIntakeWithFood.food
                val quantityInGrams = dailyIntakeWithFood.quantity
                val factor = quantityInGrams / 100.0 // Fattore per calcolare i nutrienti in base alla quantità

                totalEnergy += food.energyKcal * factor // Usa l'operatore Elvis per i nullable
                totalProtein += food.protein * factor
                totalFat += food.fat * factor
                totalSaturatedFat += food.saturatedFat * factor
                totalCarbs += food.carbohydrates * factor
                totalFiber += food.fiber * factor     // Aggiungi se hai questi campi
                totalSugar += food.sugars * factor // Aggiungi se hai questi campi
                totalSalt += food.salt * factor
            }

            NutrientTotalsUiModel(
                totalEnergyKcal = NutrientUiModel(totalEnergy, RecommendedQuantities.ENERGY_KCAL, "kcal"),
                totalProteinGrams = NutrientUiModel(totalProtein, RecommendedQuantities.PROTEINS, "g"),
                totalFatGrams = NutrientUiModel(totalFat, RecommendedQuantities.FAT, "g"),
                totalSaturatedFatGrams = NutrientUiModel(totalSaturatedFat, RecommendedQuantities.SATURATED_FAT, "g"),
                totalCarbsGrams = NutrientUiModel(totalCarbs, RecommendedQuantities.CARBOHYDRATES, "g"),
                totalFiberGrams = NutrientUiModel(totalFiber, RecommendedQuantities.FIBERS, "g"),
                totalSugarGrams = NutrientUiModel(totalSugar, RecommendedQuantities.SUGAR, "g"),
                totalSaltGrams = NutrientUiModel(totalSalt, RecommendedQuantities.SALT, "g")
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    object RecommendedQuantities {
        const val ENERGY_KCAL = 2900.0
        const val PROTEINS = 130.0
        const val CARBOHYDRATES = 375.0
        const val FAT = 90.0
        const val SATURATED_FAT = 30.0
        const val FIBERS = 30.0
        const val SUGAR = 50.0
        const val SALT = 5.0
    }
}

data class NutrientUiModel(val currentValue: Double, val targetValue: Double, val unit: String) {
    val percentageOfTarget by lazy { (currentValue / targetValue).toFloat() }
}