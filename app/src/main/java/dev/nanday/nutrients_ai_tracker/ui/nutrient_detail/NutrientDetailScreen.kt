package dev.nanday.nutrients_ai_tracker.ui.nutrient_detail // o il package che preferisci

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.nanday.nutrients_ai_tracker.data.models.Nutrient
import dev.nanday.nutrients_ai_tracker.ui.NutrientProgressRow
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutrientDetailScreen(
    viewModel: NutrientDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.nutrientDetailState.collectAsState()
    val nutrientProgress = state.nutrientProgress
    val contributingFoods = state.uiModels

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dettaglio: ${state.nutrientName}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (nutrientProgress == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // 1. Barra di progresso in alto
                NutrientProgressRow(
                    label = state.nutrientName ?: "", // Usa il nome del nutriente passato
                    uiModel = nutrientProgress,
                    // Scegli un colore appropriato o passalo dinamicamente
                    barColor = getNutrientColor(viewModel.nutrient),
                    onClick = {} // Non serve azione di click qui
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Alimenti che contribuiscono:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 2. Lista degli alimenti che contribuiscono
                if (contributingFoods.isEmpty()) {
                    Text("Nessun alimento specifico ha contribuito a questo nutriente oggi, oppure i dati non sono disponibili.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(contributingFoods, key = { it.id }) { foodItem ->
                            ContributingFoodListItem(item = foodItem)
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContributingFoodListItem(item: ContributingFoodItemUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text("Consumato: ${item.amountConsumed}", style = MaterialTheme.typography.bodySmall)
        }
        Text(
            text = "${String.format(Locale.getDefault(), "%.1f", item.nutrientContribution)} ${item.nutrientUnit}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = getNutrientColor(item.nutrient)
        )
    }
}

fun getNutrientColor(nutrient: Nutrient): Color {
    return when (nutrient) {
        Nutrient.CALORIES -> Color(0xFFE57373) // Esempio
        Nutrient.PROTEINS -> Color(0xFF4CAF50)
        Nutrient.FAT -> Color(0xFFFFC107)
        Nutrient.SATURATED_FAT -> Color(0xFFBB9900)
        Nutrient.CARBOHYDRATES -> Color(0xFF2196F3)
        Nutrient.SUGAR -> Color(0xFFE91E63)
        Nutrient.FIBERS -> Color(0xFF795548)
        Nutrient.SALT -> Color(0xFF553344)
    }
}