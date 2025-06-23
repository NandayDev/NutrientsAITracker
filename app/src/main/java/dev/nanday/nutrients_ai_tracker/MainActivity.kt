package dev.nanday.nutrients_ai_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.nanday.nutrients_ai_tracker.ui.HomeScreen
import dev.nanday.nutrients_ai_tracker.ui.HomeViewModel
import dev.nanday.nutrients_ai_tracker.ui.add_food.AddFoodScreen
import dev.nanday.nutrients_ai_tracker.ui.food_list.FoodListScreen
import dev.nanday.nutrients_ai_tracker.ui.food_list.FoodListViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val homeViewModel: HomeViewModel = hiltViewModel() // Replace with your actual HomeViewModel
            val foodListViewModel: FoodListViewModel = hiltViewModel() // Replace with your actual FoodListViewModel
            AppNavigation(homeViewModel = homeViewModel, foodListViewModel = foodListViewModel)
        }
    }
}

@Composable
fun AppNavigation(homeViewModel: HomeViewModel, foodListViewModel: FoodListViewModel /*, addFoodViewModel: AddFoodViewModel */) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onAddFood = { navController.navigate(Screen.FoodList.route) } // Navigate to FoodList
            )
        }
        composable(Screen.FoodList.route) {
            // You'll create this Composable in the next step
            FoodListScreen(
                viewModel = foodListViewModel,
                onNavigateToAddFood = { navController.navigate(Screen.AddFood.route) }, // Navigate to AddFood screen
                onFoodSelected = { foodId ->
                    // Handle what happens when a food is selected
                    // For example, navigate back to Home or another screen, passing the selected foodId
                    navController.popBackStack() // Example: Go back to the previous screen
                }
            )
        }
        composable(Screen.AddFood.route) {
            AddFoodScreen(
                // addFoodViewModel = hiltViewModel(), // If using Hilt
                onFoodAdded = {
                    navController.popBackStack(Screen.FoodList.route, inclusive = false) // Or navigate to FoodList
                    // Optionally, you could navigate all the way back to Home:
                    // navController.popBackStack(Screen.Home.route, inclusive = false)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object FoodList : Screen("food_list")
    object AddFood : Screen("add_food")
}