package dev.nanday.nutrients_ai_tracker.ui.daily_intake

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import dev.nanday.nutrients_ai_tracker.data.models.NutritionalTargets
import dev.nanday.nutrients_ai_tracker.ui.shared.SimpleYesCancelDialog
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

// Helper function to determine color
@Composable
fun getPercentageColor(percentage: Double, isFiber: Boolean = false): Color {
    val p = percentage.roundToInt()
    val green = Color(0xFF4CAF50)
    val orange = Color(0xFFFF9800)
    val red = Color(0xFFF44336)

    return if (isFiber) {
        when {
            p > 20 -> green
            p in 10..20 -> orange
            else -> red
        }
    } else {
        when {
            p > 20 -> red
            p in 10..20 -> orange
            else -> green
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyIntakeScreen(
    viewModel: AddDailyIntakeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val food by viewModel.food.collectAsState()
    val shouldShowDeleteButton by viewModel.shouldShowDeleteButton.collectAsState()
    val grams by viewModel.grams
    val calculatedNutrients by viewModel.calculatedNutrients.collectAsState()
    val targets by viewModel.targets.collectAsState()
    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope
    val context = LocalContext.current

    var showDeleteAlertDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(food?.name ?: "Dettaglio Alimento") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                },
                actions = {
                    if (shouldShowDeleteButton) {
                        IconButton(onClick = {
                            showDeleteAlertDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Seleziona Data"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (grams.isNotBlank() && (grams.toIntOrNull() ?: 0) > 0 && food != null) {
                        lifecycleScope.launch {
                            viewModel.onSaveClicked()
                            Toast.makeText(context, "Salvato!", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Inserisci i grammi prima di proseguire",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                icon = { Icon(Icons.Filled.Save, "Salva porzione") }, // Choose an appropriate icon
                text = { Text("Salva porzione") },
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        food?.let { currentFood ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()), // Per rendere scrollabile se il contenuto è lungo
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = currentFood.name,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                currentFood.photo?.let { photoBytes ->
                    val bitmap = try {
                        BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
                    } catch (_: Exception) {
                        null
                    }
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Foto di ${currentFood.name}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f) // Esempio di aspect ratio
                                .padding(vertical = 8.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                } ?: Box( // Placeholder se non c'è foto
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally) // Allinea il Box al centro
                    // .background(MaterialTheme.colorScheme.surfaceVariant) // Colore per il placeholder
                ) {
                    Text(
                        "Nessuna foto disponibile",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }


                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = grams,
                    onValueChange = { viewModel.onGramsChanged(it) },
                    label = { Text("Quantità (grammi)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Valori Nutrizionali (per ${grams.ifBlank { "0" }}g)",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                calculatedNutrients?.let { nutrients ->
                    NutrientTable(nutrients, targets, viewModel)
                } ?: Text("Inserisci i grammi per calcolare i nutrienti.")
            }
        } ?: run {
            // Mostra un caricamento o un messaggio di errore se il cibo non è stato trovato/caricato
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    if (showDeleteAlertDialog) {
        SimpleYesCancelDialog(
            text = "Vuoi davvero eliminare questa entry?",
            onConfirm = {
                showDeleteAlertDialog = false
                lifecycleScope.launch {
                    viewModel.delete()
                    Toast.makeText(context, "Eliminato!", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                }
            },
            onDismiss = {
                showDeleteAlertDialog = false
            }
        )
    }
}

@Composable
fun NutrientTable(
    nutrientInfo: NutrientInfo,
    targets: NutritionalTargets,
    viewModel: AddDailyIntakeViewModel
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        NutrientRow(
            "Calorie",
            String.format(Locale.ITALY, "%.1f", nutrientInfo.energyKcal),
            "kcal",
            viewModel.calculatePercentage(nutrientInfo.energyKcal, targets.calories)
        )
        NutrientRow(
            "Grassi",
            String.format(Locale.ITALY, "%.1f", nutrientInfo.fat),
            "g",
            viewModel.calculatePercentage(nutrientInfo.fat, targets.fat)
        )
        NutrientRow(
            "Di cui saturi",
            String.format(Locale.ITALY, "%.1f", nutrientInfo.saturatedFat),
            "g",
            viewModel.calculatePercentage(nutrientInfo.saturatedFat, targets.saturatedFat)
        )
        NutrientRow(
            "Carboidrati",
            String.format(Locale.ITALY, "%.1f", nutrientInfo.carbohydrates),
            "g",
            viewModel.calculatePercentage(nutrientInfo.carbohydrates, targets.carbohydrates)
        )
        NutrientRow(
            "Zuccheri",
            String.format(Locale.ITALY, "%.1f", nutrientInfo.sugars),
            "g",
            viewModel.calculatePercentage(nutrientInfo.sugars, targets.sugars)
        )
        NutrientRow(
            "Fibre",
            String.format(Locale.ITALY, "%.1f", nutrientInfo.fiber),
            "g",
            viewModel.calculatePercentage(nutrientInfo.fiber, targets.fiber),
            isFiber = true
        )
        NutrientRow(
            "Proteine",
            String.format(Locale.ITALY, "%.1f", nutrientInfo.protein),
            "g",
            viewModel.calculatePercentage(nutrientInfo.protein, targets.protein)
        )
        NutrientRow(
            "Sale",
            String.format(Locale.ITALY, "%.1f", nutrientInfo.salt),
            "g",
            viewModel.calculatePercentage(nutrientInfo.salt, targets.salt)
        )
    }
}

@Composable
fun NutrientRow(
    label: String,
    value: String,
    unit: String,
    percentage: Double? = null,
    isFiber: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Row {
            Text(
                "$value $unit",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            if (percentage != null && percentage > 0) {
                Text(
                    " (${percentage.roundToInt()}%)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = getPercentageColor(percentage, isFiber)
                )
            }
        }
    }
}
