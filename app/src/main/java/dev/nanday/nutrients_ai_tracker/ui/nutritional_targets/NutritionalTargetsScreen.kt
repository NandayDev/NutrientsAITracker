package dev.nanday.nutrients_ai_tracker.ui.nutritional_targets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionalTargetsScreen(
    viewModel: NutritionalTargetViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val calories by viewModel.calories
    val protein by viewModel.protein
    val fat by viewModel.fat
    val saturatedFat by viewModel.saturatedFat
    val carbohydrates by viewModel.carbohydrates
    val sugars by viewModel.sugars
    val fiber by viewModel.fiber
    val salt by viewModel.salt

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modifica Target Nutrizionali") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.saveTargets()
                onNavigateBack() // O mostra un messaggio e resta, o torna indietro
            }) {
                Icon(Icons.Filled.Check, contentDescription = "Salva Target")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TargetSlider(
                label = "Calorie (kcal)",
                value = calories,
                onValueChange = { viewModel.onCaloriesChange(it) },
                valueRange = NutritionalTargetsConstants.CALORIES_MIN..NutritionalTargetsConstants.CALORIES_MAX,
                stepSize = NutritionalTargetsConstants.CALORIES_STEP_SIZE
            )
            TargetSlider(
                label = "Proteine (g)",
                value = protein,
                onValueChange = { viewModel.onProteinChange(it) },
                valueRange = NutritionalTargetsConstants.PROTEINS_MIN..NutritionalTargetsConstants.PROTEINS_MAX,
                stepSize = NutritionalTargetsConstants.PROTEINS_STEP_SIZE
            )
            TargetSlider(
                label = "Grassi (g)",
                value = fat,
                onValueChange = { viewModel.onFatChange(it) },
                valueRange = NutritionalTargetsConstants.FAT_MIN..NutritionalTargetsConstants.FAT_MAX,
                stepSize = NutritionalTargetsConstants.FAT_STEP_SIZE
            )
            TargetSlider(
                label = "Grassi Saturi (g)",
                value = saturatedFat,
                onValueChange = { viewModel.onSaturatedFatChange(it) },
                valueRange = NutritionalTargetsConstants.SATURATED_FAT_MIN..NutritionalTargetsConstants.SATURATED_FAT_MAX,
                stepSize = NutritionalTargetsConstants.SATURATED_FAT_STEP_SIZE
            )
            TargetSlider(
                label = "Carboidrati (g)",
                value = carbohydrates,
                onValueChange = { viewModel.onCarbohydratesChange(it) },
                valueRange = NutritionalTargetsConstants.CARBOHYDRATES_MIN..NutritionalTargetsConstants.CARBOHYDRATES_MAX,
                stepSize = NutritionalTargetsConstants.CARBOHYDRATES_STEP_SIZE
            )
            TargetSlider(
                label = "Zuccheri (g)",
                value = sugars,
                onValueChange = { viewModel.onSugarsChange(it) },
                valueRange = NutritionalTargetsConstants.SUGAR_MIN..NutritionalTargetsConstants.SUGAR_MAX,
                stepSize = NutritionalTargetsConstants.SUGAR_STEP_SIZE
            )
            TargetSlider(
                label = "Fibre (g)",
                value = fiber,
                onValueChange = { viewModel.onFiberChange(it) },
                valueRange = NutritionalTargetsConstants.FIBERS_MIN..NutritionalTargetsConstants.FIBERS_MAX,
                stepSize = NutritionalTargetsConstants.FIBERS_STEP_SIZE
            )
            TargetSlider(
                label = "Sale (g)",
                value = salt,
                onValueChange = { viewModel.onSaltChange(it) },
                valueRange = NutritionalTargetsConstants.SALT_MIN..NutritionalTargetsConstants.SALT_MAX,
                stepSize = NutritionalTargetsConstants.SALT_STEP_SIZE
            )

            Spacer(modifier = Modifier.height(72.dp)) // Spazio per il FAB
        }
    }
}

@Composable
fun TargetSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    stepSize: Float,
    unit: String = "" // Opzionale per unità se non già nel label
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${value.roundToInt()}$unit", // Mostra il valore arrotondato
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = ((valueRange.endInclusive - valueRange.start) / stepSize - 1).roundToInt(), // Numero di segmenti discreti tra min e max. stepSize = N-1 se vuoi N valori possibili
            modifier = Modifier.fillMaxWidth()
        )
    }
}
