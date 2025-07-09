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
import dev.nanday.nutrients_ai_tracker.ui.daily_intake.DailyIntakeScreen
import dev.nanday.nutrients_ai_tracker.ui.food_detail.FoodDetailScreen
import dev.nanday.nutrients_ai_tracker.ui.food_list.FoodListScreen
import dev.nanday.nutrients_ai_tracker.ui.food_list.FoodListViewModel
import dev.nanday.nutrients_ai_tracker.ui.nutrient_detail.NutrientDetailScreen
import dev.nanday.nutrients_ai_tracker.ui.nutritional_targets.NutritionalTargetsScreen

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
                onAddDailyIntake = { navController.navigate(Screen.FoodListForDailyIntake.route) },
                onUpdateDailyIntake = { dailyIntakeId ->
                    navController.navigate(
                        Screen.UpdateDailyIntake.createRoute(dailyIntakeId)
                    )
                },
                onNutrientClick = { nutrient ->
                    navController.navigate(
                        Screen.NutrientDetail.createRoute(nutrient.number)
                    )
                },
                onShowFoodList = {
                    navController.navigate(Screen.FoodListForFoodEdit.route)
                },
                onNavigateToEditTargets = {
                    navController.navigate(Screen.NutritionalTargets.route)
                }
            )
        }
        composable(Screen.FoodListForDailyIntake.route) {
            FoodListScreen(
                viewModel = foodListViewModel,
                onNavigateToAddFood = { navController.navigate(Screen.AddFood.route) }, // Navigate to AddFood screen
                onFoodClick = { food ->
                    val route = Screen.AddDailyIntake.createRoute(food.id)
                    navController.navigate(route)
                }
            )
        }
        composable(Screen.FoodListForFoodEdit.route) {
            FoodListScreen(
                viewModel = foodListViewModel,
                onNavigateToAddFood = { navController.navigate(Screen.AddFood.route) }, // Navigate to AddFood screen
                onFoodClick = { food ->
                    val route = Screen.UpdateFood.createRoute(food.id)
                    navController.navigate(route)
                }
            )
        }
        composable(Screen.AddFood.route) {
            FoodDetailScreen(
                // addFoodViewModel = hiltViewModel(), // If using Hilt
                onFoodAdded = {
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack(Screen.Home.route, false, false) }
            )
        }
        composable(Screen.UpdateFood.route, arguments = listOf(navArgument("foodId") { type = NavType.LongType })) {
            FoodDetailScreen(
                // addFoodViewModel = hiltViewModel(), // If using Hilt
                onFoodAdded = {
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack(Screen.Home.route, false, false) }
            )
        }
        composable(Screen.AddDailyIntake.route, arguments = listOf(navArgument("foodId") { type = NavType.LongType })) {
            DailyIntakeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.UpdateDailyIntake.route, arguments = listOf(navArgument("dailyIntakeId") { type = NavType.LongType })) {
            DailyIntakeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.NutrientDetail.route,
            arguments = listOf(navArgument("nutrientNumber") { type = NavType.IntType })
        ) { backStackEntry ->
            val nutrientNumber = backStackEntry.arguments?.getInt("nutrientNumber")
            if (nutrientNumber != null) {
                NutrientDetailScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.NutritionalTargets.route) {
            NutritionalTargetsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object FoodListForDailyIntake : Screen("food_list_daily_intake")
    object FoodListForFoodEdit : Screen("food_list_edit")
    object AddFood : Screen("add_food")
    object UpdateFood : Screen("update_food/{foodId}") {
        fun createRoute(foodId: Long): String = "update_food/$foodId"
    }

    object AddDailyIntake : Screen("add_daily_intake/{foodId}") {
        fun createRoute(foodId: Long): String = "add_daily_intake/$foodId"
    }

    object UpdateDailyIntake : Screen("update_daily_intake/{dailyIntakeId}") {
        fun createRoute(dailyIntakeId: Long): String = "update_daily_intake/$dailyIntakeId"
    }

    object NutrientDetail : Screen("nutrient_detail/{nutrientNumber}") {
        fun createRoute(nutrientNumber: Int): String = "nutrient_detail/$nutrientNumber"
    }

    object NutritionalTargets : Screen("nutritional_targets")
}