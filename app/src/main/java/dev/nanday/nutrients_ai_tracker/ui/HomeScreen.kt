package dev.nanday.nutrients_ai_tracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.nanday.nutrients_ai_tracker.data.models.Nutrient
import dev.nanday.nutrients_ai_tracker.ui.date_picker.DateChoiceDialog
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddDailyIntake: () -> Unit,
    onShowFoodList: () -> Unit,
    onUpdateDailyIntake: (Long) -> Unit,
    onNutrientClick: (Nutrient) -> Unit
) {
    val uiModels by viewModel.uiModels.collectAsState()
    val nutrientTotals by viewModel.nutrientTotalsUi.collectAsState()
    var showDatePickerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riepilogo Giornaliero") },
                actions = {
                    IconButton(onClick = { showDatePickerDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.CalendarToday,
                            contentDescription = "Seleziona Data"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Row {
                FloatingActionButton(
                    modifier = Modifier.padding(end = 15.dp),
                    onClick = onShowFoodList
                ) {
                    Icon(Icons.Filled.Fastfood, contentDescription = "Lista cibi")
                }
                FloatingActionButton(onClick = onAddDailyIntake) {
                    Icon(Icons.Filled.Add, contentDescription = "Aggiungi Consumazione")
                }
            }

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Sezione Lista Alimenti Consumati (limitata in altezza)
            Text(
                "Alimenti Consumati Oggi:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (uiModels.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Occupa lo spazio disponibile nella parte superiore
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nessun alimento consumato oggi.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(
                        items = uiModels,
                        key = { it.id } // Assicurati che DailyIntakeUiModel abbia un ID univoco
                    ) { uiModel ->
                        ConsumedFoodItem(foodItem = uiModel) {
                            onUpdateDailyIntake(uiModel.id)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Sezione Riepilogo Nutrienti Totali
            Text(
                "Riepilogo Nutrienti Totali:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            NutrientTotalsTable(
                totals = nutrientTotals,
                onNutrientClick = onNutrientClick
            )

            Spacer(modifier = Modifier.weight(0.3f)) // Un po' di spazio in fondo, se necessario
        }
    }

    if (showDatePickerDialog) {
        DateChoiceDialog(
            onDismiss = {
                showDatePickerDialog = false
            }
        )
    }
}

@Composable
fun ConsumedFoodItem(foodItem: DailyIntakeUiModel, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(
                onClick = onClick,
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() }
            )
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = foodItem.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f) // Permette al nome di andare a capo se lungo
        )
        Text(
            text = foodItem.quantity,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun NutrientTotalsTable(
    totals: NutrientTotalsUiModel?,
    onNutrientClick: (Nutrient) -> Unit
) {
    if (totals == null) {
        CircularProgressIndicator()
        return
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            NutrientProgressRow(
                label = "Calorie",
                uiModel = totals.totalEnergyKcal,
                barColor = MaterialTheme.colorScheme.tertiary, // Colore per le calorie
                onClick = onNutrientClick
            )
            NutrientProgressRow(
                label = "Proteine",
                uiModel = totals.totalProteinGrams,
                barColor = Color(0xFF4CAF50), // Verde per le proteine
                onClick = onNutrientClick
            )
            NutrientProgressRow(
                label = "Grassi",
                uiModel = totals.totalFatGrams,
                barColor = Color(0xFFFFC107), // Giallo/Arancio per i grassi
                onClick = onNutrientClick
            )
            NutrientProgressRow(
                label = "Grassi saturi",
                uiModel = totals.totalSaturatedFatGrams,
                barColor = Color(0xFFBB9900), // Giallo/Arancio per i grassi
                onClick = onNutrientClick
            )
            NutrientProgressRow(
                label = "Carboidrati",
                uiModel = totals.totalCarbsGrams,
                barColor = Color(0xFF2196F3), // Blu per i carboidrati
                onClick = onNutrientClick
            )
            NutrientProgressRow(
                label = "Zuccheri", // Ricorda che questo è un limite massimo
                uiModel = totals.totalSugarGrams,
                barColor = if (totals.totalSugarGrams.percentageOfTarget > 1f) MaterialTheme.colorScheme.error else Color(0xFFE91E63), // Rosa/Rosso
                onClick = onNutrientClick
            )
            NutrientProgressRow(
                label = "Fibre",
                uiModel = totals.totalFiberGrams,
                barColor = Color(0xFF795548), // Marrone per le fibre
                onClick = onNutrientClick
            )
            NutrientProgressRow(
                label = "Sale", // Ricorda che questo è un limite massimo
                uiModel = totals.totalSaltGrams,
                barColor = if (totals.totalSaltGrams.percentageOfTarget > 1f) MaterialTheme.colorScheme.error else Color(0xFF553344), // Nero
                onClick = onNutrientClick
            )
        }
    }
}


@Composable
fun NutrientProgressRow(
    label: String,
    uiModel: NutrientUiModel,
    barColor: Color,
    barBackgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    textColorOnBar: Color = Color.White,
    textColorOffBar: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (Nutrient) -> Unit
) {
    val percentageString = String.format(Locale.getDefault(), "%.0f", uiModel.percentageOfTarget * 100)
    val progressFraction = kotlin.math.min(uiModel.percentageOfTarget, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // Fa sì che il Box si adatti all'altezza del contenuto (Row)
            .clip(RoundedCornerShape(8.dp)) // Arrotonda gli angoli dell'intera riga/barra
            .clickable(
                onClick = { onClick(uiModel.nutrient) },
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        // 1. Sfondo completo (barBackgroundColor)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(barBackgroundColor)
        )

        // 2. Barra di progresso colorata (barColor)
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = progressFraction)
                .background(barColor)
        )

        // 3. Contenuto testuale sovrapposto
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp), // Padding interno per il testo
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Testo a sinistra: Nome Nutriente (XX%)
            Text(
                text = "$label ($percentageString%)",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                // Il colore del testo cambia se è sopra la parte colorata o lo sfondo
                // Questo è un trucco: assumiamo che il testo a sinistra sarà "sopra" la barra
                // se la barra si estende abbastanza. È una semplificazione.
                // Una soluzione più robusta richiederebbe di misurare/calcolare le posizioni.
                color = if (progressFraction > 0.05) textColorOnBar else textColorOffBar, // Piccola soglia per cambiare colore
                modifier = Modifier.weight(1f) // Occupa più spazio possibile, spingendo il testo a destra
            )

            // Testo a destra: Valori
            Text(
                text = "${String.format(Locale.getDefault(), "%.0f", uiModel.currentValue)} / ${String.format(Locale.getDefault(), "%.0f", uiModel.targetValue)} ${uiModel.unit}",
                style = MaterialTheme.typography.bodyMedium,
                // Il testo a destra è più probabile che sia sulla parte non colorata (o alla fine della parte colorata)
                // Questo è ancora una semplificazione.
                color = textColorOffBar,
                textAlign = TextAlign.End
            )
        }
    }
    Spacer(modifier = Modifier.height(6.dp)) // Spazio tra le righe di nutrienti
}
