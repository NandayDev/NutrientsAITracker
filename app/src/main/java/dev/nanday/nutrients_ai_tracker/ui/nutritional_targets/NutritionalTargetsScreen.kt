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
                valueRange = 1500f..4000f,
                steps = (4000 - 1500) / 100 - 1 // steps = (numMax - numMin) / stepSize - 1
            )
            TargetSlider(
                label = "Proteine (g)",
                value = protein,
                onValueChange = { viewModel.onProteinChange(it) },
                valueRange = 50f..200f,
                steps = (200 - 50) / 5 - 1
            )
            TargetSlider(
                label = "Grassi (g)",
                value = fat,
                onValueChange = { viewModel.onFatChange(it) },
                valueRange = 40f..150f,
                steps = (150 - 40) / 5 - 1
            )
            TargetSlider(
                label = "Grassi Saturi (g)",
                value = saturatedFat,
                onValueChange = { viewModel.onSaturatedFatChange(it) },
                valueRange = 10f..60f,
                steps = (60 - 10) / 5 - 1
            )
            TargetSlider(
                label = "Carboidrati (g)",
                value = carbohydrates,
                onValueChange = { viewModel.onCarbohydratesChange(it) },
                valueRange = 200f..500f,
                steps = (500 - 200) / 10 - 1
            )
            TargetSlider(
                label = "Zuccheri (g)",
                value = sugars,
                onValueChange = { viewModel.onSugarsChange(it) },
                valueRange = 30f..100f,
                steps = (100 - 30) / 5 - 1
            )
            TargetSlider(
                label = "Fibre (g)",
                value = fiber,
                onValueChange = { viewModel.onFiberChange(it) },
                valueRange = 10f..100f,
                steps = (100 - 10) / 5 - 1
            )
            TargetSlider(
                label = "Sale (g)",
                value = salt,
                onValueChange = { viewModel.onSaltChange(it) },
                valueRange = 0f..20f,
                steps = (20 - 0) / 1 - 1
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
    steps: Int,
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
            steps = steps, // Numero di segmenti discreti tra min e max. steps = N-1 se vuoi N valori possibili
            modifier = Modifier.fillMaxWidth()
        )
    }
}
