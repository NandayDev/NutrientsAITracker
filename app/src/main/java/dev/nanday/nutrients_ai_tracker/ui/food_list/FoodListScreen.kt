package dev.nanday.nutrients_ai_tracker.ui.food_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Assume you have a FoodListViewModel that exposes a list of foods
// and a data class FoodItem (replace with your actual data class)
data class FoodItem(val id: String, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodListScreen(
    viewModel: FoodListViewModel,
    onNavigateToAddFood: () -> Unit,
    onFoodSelected: (Long) -> Unit // Callback with the ID of the selected food
) {
    val foods by viewModel.allFoods.collectAsState()

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
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFoodSelected(food.id) } // Handle food selection
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(food.name, style = MaterialTheme.typography.headlineSmall)
                        // Add other food details if needed
                    }
                }
            }
        }
    }
}