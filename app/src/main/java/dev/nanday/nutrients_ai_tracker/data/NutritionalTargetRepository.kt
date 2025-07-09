package dev.nanday.nutrients_ai_tracker.data

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.nanday.nutrients_ai_tracker.data.models.NutritionalTargets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface NutritionalTargetRepository {
    val targetsFlow: StateFlow<NutritionalTargets>
    suspend fun saveTargets(targets: NutritionalTargets)
}

@Singleton
class NutritionalTargetRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NutritionalTargetRepository {

    private val sharedPreferences = context.getSharedPreferences("Nutrients_AI_Tracker", Context.MODE_PRIVATE)

    private val _targetsFlow = MutableStateFlow(
        NutritionalTargets(
            calories = sharedPreferences.getInt(PreferencesKeys.CALORIES, PreferencesKeys.CALORIES_DEFAULT),
            protein = sharedPreferences.getInt(PreferencesKeys.PROTEIN, PreferencesKeys.PROTEIN_DEFAULT),
            fat = sharedPreferences.getInt(PreferencesKeys.FAT, PreferencesKeys.FAT_DEFAULT),
            saturatedFat = sharedPreferences.getInt(PreferencesKeys.SATURATED_FAT, PreferencesKeys.SATURATED_FAT_DEFAULT),
            carbohydrates = sharedPreferences.getInt(PreferencesKeys.CARBOHYDRATES, PreferencesKeys.CARBOHYDRATES_DEFAULT),
            sugars = sharedPreferences.getInt(PreferencesKeys.SUGARS, PreferencesKeys.SUGARS_DEFAULT),
            fiber = sharedPreferences.getInt(PreferencesKeys.FIBER, PreferencesKeys.FIBER_DEFAULT),
            salt = sharedPreferences.getInt(PreferencesKeys.SALT, PreferencesKeys.SALT_DEFAULT),
        )
    )
    override val targetsFlow = _targetsFlow.asStateFlow()

    private object PreferencesKeys {
        const val CALORIES = "target_calories"
        const val CALORIES_DEFAULT = 2900
        const val PROTEIN = "target_protein"
        const val PROTEIN_DEFAULT = 130
        const val FAT = "target_fat"
        const val FAT_DEFAULT = 90
        const val SATURATED_FAT = "target_saturated_fat"
        const val SATURATED_FAT_DEFAULT = 30
        const val CARBOHYDRATES = "target_carbohydrates"
        const val CARBOHYDRATES_DEFAULT = 375
        const val SUGARS = "target_sugars"
        const val SUGARS_DEFAULT = 50
        const val FIBER = "target_fiber"
        const val FIBER_DEFAULT = 30
        const val SALT = "target_salt"
        const val SALT_DEFAULT = 5
    }

    override suspend fun saveTargets(targets: NutritionalTargets) {
        _targetsFlow.value = targets
        sharedPreferences.edit {
            putInt(PreferencesKeys.CALORIES, targets.calories)
            putInt(PreferencesKeys.PROTEIN, targets.protein)
            putInt(PreferencesKeys.FAT, targets.fat)
            putInt(PreferencesKeys.SATURATED_FAT, targets.saturatedFat)
            putInt(PreferencesKeys.CARBOHYDRATES, targets.carbohydrates)
            putInt(PreferencesKeys.SUGARS, targets.sugars)
            putInt(PreferencesKeys.FIBER, targets.fiber)
            putInt(PreferencesKeys.SALT, targets.salt)
        }
    }
}
