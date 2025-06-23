package dev.nanday.nutrients_ai_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import dev.nanday.nutrients_ai_tracker.ui.HomeScreen
import dev.nanday.nutrients_ai_tracker.ui.HomeViewModel
import dev.nanday.nutrients_ai_tracker.ui.add_daily_intake.AddDailyIntakeScreen
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
fun AppNavigation(homeViewModel: HomeViewModel, foodListViewModel: FoodListViewModel) {
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
                onFoodClick = { food ->
                    navController.navigate(Screen.AddDailyIntake.createRoute(food.id))
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
                onNavigateBack = { navController.popBackStack(Screen.Home.route, false, false) }
            )
        }
        composable(Screen.AddDailyIntake.route, arguments = listOf(navArgument("foodId") { type = NavType.LongType })) {
            AddDailyIntakeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object FoodList : Screen("food_list")
    object AddFood : Screen("add_food")
    object AddDailyIntake : Screen("add_daily_intake/{foodId}") {
        fun createRoute(foodId: Long): String = "add_daily_intake/$foodId"
    }
}