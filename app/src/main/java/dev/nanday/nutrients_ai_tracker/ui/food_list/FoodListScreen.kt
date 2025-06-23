package dev.nanday.nutrients_ai_tracker.ui.food_list

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dev.nanday.nutrients_ai_tracker.data.models.Food

// Assume you have a FoodListViewModel that exposes a list of foods
// and a data class FoodItem (replace with your actual data class)
data class FoodItem(val id: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodListScreen(
    viewModel: FoodListViewModel,
    onNavigateToAddFood: () -> Unit,
    onFoodClick: (Food) -> Unit // Callback with the ID of the selected food
) {
    val foods by viewModel.allFoods.collectAsState(initial = listOf())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Seleziona Alimento") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddFood) {
                Icon(Icons.Default.Add, contentDescription = "Aggiungi nuovo alimento")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(foods) { food ->
                FoodItem(food, onFoodClick = onFoodClick)
            }
        }
    }
}

@Composable
fun FoodItem(
    food: Food,
    onFoodClick: (Food) -> Unit,
    modifier: Modifier = Modifier // Aggiunto modifier per passarlo dall'esterno
) {
    Card(
        modifier = modifier // Usa il modifier passato
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        onClick = { onFoodClick(food) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically // Allinea verticalmente gli elementi
        ) {
            // Visualizza l'immagine se presente
            food.photo?.let { byteArray ->
                // Tentativo di decodificare il ByteArray in un Bitmap
                val bitmap = try {
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                } catch (e: Exception) {
                    // Gestisci l'errore di decodifica, es. logga o mostra un placeholder
                    e.printStackTrace()
                    null
                }

                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Foto di ${food.name}",
                        modifier = Modifier
                            .size(60.dp) // Imposta una dimensione fissa per l'immagine
                            .clip(CircleShape), // Opzionale: rende l'immagine circolare
                        contentScale = ContentScale.Crop // Scala l'immagine per riempire lo spazio, tagliando se necessario
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp)) // Spazio tra l'immagine e il testo

            // Colonna per Nome e Calorie (o altri dettagli)
            Column(
                modifier = Modifier.weight(1f) // Occupa lo spazio rimanente
            ) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            // Puoi aggiungere un'icona o un pulsante a destra qui, se necessario
            // Icon(Icons.Filled.ChevronRight, contentDescription = "Dettagli")
        }
    }
}