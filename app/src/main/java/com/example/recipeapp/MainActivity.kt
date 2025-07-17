// Enhanced MainActivity.kt - Gradual approach
package com.example.recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.recipeapp.ui.theme.RecipeAppTheme
import com.example.recipeapp.view.FavoritesScreen
import com.example.recipeapp.view.MealDetailScreen
import com.example.recipeapp.view.MealsScreen
import com.example.recipeapp.view.RecipeScreen
import com.example.recipeapp.view.SearchScreen
import com.example.recipeapp.view.SettingsScreen

// Import new screens as you create them
// import com.example.recipeapp.view.SearchScreen
// import com.example.recipeapp.view.MealDetailScreen
// import com.example.recipeapp.view.FavoritesScreen
// import com.example.recipeapp.view.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeAppTheme {
                RecipeApp()
            }
        }
    }
}

sealed class Screen(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Categories : Screen("categories", "Categories", Icons.Default.Home)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeApp() {
    // Keep your existing navigation state
    var currentScreen by remember { mutableStateOf("categories") }
    var selectedCategory by remember { mutableStateOf("") }
    var selectedMealId by remember { mutableStateOf("") }
    var previousScreen by remember { mutableStateOf("categories") }

    val bottomNavItems = listOf(
        Screen.Categories,
        Screen.Search,
        Screen.Favorites,
        Screen.Settings
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = if (currentScreen in listOf(
                "categories",
                "search",
                "favorites",
                "settings"
            )
        ) {
            WindowInsets.systemBars.exclude(WindowInsets.navigationBars)
        } else {
            WindowInsets.systemBars
        },
        bottomBar = {
            if (currentScreen in listOf("categories", "search", "favorites", "settings")) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentScreen == screen.route,
                            onClick = {
                                currentScreen = screen.route
                                previousScreen = currentScreen
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            "categories" -> {
                RecipeScreen(
                    modifier = Modifier.fillMaxSize()
                        .padding(innerPadding)
                        .windowInsetsPadding(WindowInsets.statusBars),
                    onCategoryClick = { category ->
                        selectedCategory = category
                        previousScreen = currentScreen
                        currentScreen = "meals"
                    }
                )
            }

            "meals" -> {
                MealsScreen(
                    category = selectedCategory,
                    onBackClick = { currentScreen = previousScreen },
                    onMealClick = { mealId ->
                        selectedMealId = mealId
                        currentScreen = "meal_detail"
                    }
                )
            }

            "meal_detail" -> {
                MealDetailScreen(
                    mealId = selectedMealId,
                    onBackClick = { currentScreen = previousScreen })
            }

            "search" -> {
                SearchScreen(
                    onMealClick = { mealId ->
                        selectedMealId = mealId
                        previousScreen = currentScreen
                        currentScreen = "meal_detail"
                    }
                )
            }

            "favorites" -> {
                FavoritesScreen(
                    onMealClick = { mealId ->
                        selectedMealId = mealId
                        currentScreen = "meal_detail"
                    }
                )
            }

            "settings" -> {
                SettingsScreen()
            }
        }
    }
}