package com.example.recipeapp.respository

import android.content.Context
import android.content.SharedPreferences
import com.example.recipeapp.model.Area
import com.example.recipeapp.model.DietaryRestriction
import com.example.recipeapp.model.Ingredient
import com.example.recipeapp.model.Meal
import com.example.recipeapp.model.SearchFilters
import com.example.recipeapp.model.SearchHistory
import com.example.recipeapp.model.SearchSuggestion
import com.example.recipeapp.model.SuggestionType
import com.example.recipeapp.model.UiState
import com.example.recipeapp.service.NetworkModule
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepository(context: Context) {
    private val apiService = NetworkModule.apiService
    private val prefs: SharedPreferences =
        context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_SEARCH_HISTORY = "search_history"
        private const val KEY_TRENDING_SEARCHES = "trending_searches"
        private const val MAX_HISTORY_SIZE = 50
    }

    fun searchWithFilters(
        query: String,
        filters: SearchFilters
    ): Flow<UiState<List<Meal>>> = flow {
        emit(UiState.Loading)
        try {
            var results = mutableListOf<Meal>()
            var hasFilters = false

            if (query.isNotBlank()) {
                try {
                    val nameResults = apiService.searchMealsByName(query)
                    nameResults.meals?.let { results.addAll(it) }
                } catch (e: Exception) {
                    println("Text search failed: ${e.message}")
                }
            }

            // Filter by category, apply filters one by one
            filters.category?.let { category ->
                hasFilters = true
                val categoryResults = apiService.getMealsByCategory(category)
                categoryResults.meals?.let { meals ->
                    if (query.isBlank() && results.isEmpty()) {
                        results.addAll(meals)
                    } else if (results.isNotEmpty()) {
                        val categoryIds = meals.map { it.idMeal }.toSet()
                        results = results.filter { it.idMeal in categoryIds }.toMutableList()
                    } else {
                        results.addAll(meals)
                    }
                }
            }

            // Filters by area
            filters.area?.let { area ->
                hasFilters = true
                val areaResults = apiService.getMealsByArea(area)
                areaResults.meals?.let { meals ->
                    if (results.isEmpty() && query.isBlank()) {
                        results.addAll(meals)
                    } else if (results.isNotEmpty()) {
                        val areaIds = meals.map { it.idMeal }.toSet()
                        results = results.filter { it.idMeal in areaIds }.toMutableList()
                    } else {
                        results.addAll(meals)
                    }
                }
            }

            // Filter by ingredient
            filters.ingredient?.let { ingredient ->
                hasFilters = true
                val ingredientResults = apiService.getMealsByIngredient(ingredient)
                ingredientResults.meals?.let { meals ->
                    if (results.isEmpty() && query.isBlank()) {
                        results.addAll(meals)
                    } else if (results.isNotEmpty()) {
                        val ingredientIds = meals.map { it.idMeal }.toSet()
                        results = results.filter { it.idMeal in ingredientIds }.toMutableList()
                    } else {
                        results.addAll(meals)
                    }
                }
            }

            if (filters.dietaryRestrictions.isNotEmpty()) {
                hasFilters = true
            }

            if (query.isBlank() && !hasFilters) {
                emit(UiState.Success(emptyList()))
                return@flow
            }

            val filteredResults = applyDietaryFilters(results, filters.dietaryRestrictions)

            if (query.isNotBlank()) {
                saveSearchHistory(SearchHistory(query = query, filters = filters))
            }

            val uniqueResults = filteredResults.distinctBy { it.idMeal }
            emit(
                UiState.Success(
                    uniqueResults
                )
            )
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Search failed"))
        }
    }

    private fun applyDietaryFilters(
        meals: List<Meal>,
        restrictions: List<DietaryRestriction>
    ): List<Meal> {
        if (restrictions.isEmpty()) return meals

        return meals.filter { meal ->
            restrictions.all { restriction ->
                when (restriction) {
                    DietaryRestriction.VEGETARIAN -> isVegetarian(meal)
                    DietaryRestriction.VEGAN -> isVegan(meal)
                    DietaryRestriction.GLUTEN_FREE -> isGlutenFree(meal)
                    DietaryRestriction.DAIRY_FREE -> isDairyFree(meal)
                    DietaryRestriction.LOW_CARB -> isLowCarb(meal)
                    DietaryRestriction.KETO -> isKeto(meal)
                    DietaryRestriction.PALEO -> isPaleo(meal)
                }
            }
        }
    }

    private fun isVegetarian(meal: Meal): Boolean {
        val nonVegKeywords = listOf("beef", "chicken", "pork", "fish", "meat", "bacon", "ham")
        val ingredients = meal.getIngredient().map { it.first.lowercase() }
        return nonVegKeywords.none { keyword ->
            ingredients.any { it.contains(keyword) }
        }
    }

    private fun isVegan(meal: Meal): Boolean {
        val nonVegKeywords = listOf(
            "beef",
            "chicken",
            "pork",
            "fish",
            "meat",
            "bacon",
            "ham",
            "milk",
            "cheese",
            "butter",
            "cream",
            "egg",
            "honey"
        )
        val ingredients = meal.getIngredient().map { it.first.lowercase() }
        return nonVegKeywords.none { keyword ->
            ingredients.any { it.contains(keyword) }
        }
    }

    private fun isGlutenFree(meal: Meal): Boolean {
        val glutenKeywords = listOf("flour", "wheat", "bread", "pasta", "noddles", "soy sauce")
        val ingredients = meal.getIngredient().map { it.first.lowercase() }
        return glutenKeywords.none { keyword ->
            ingredients.any { it.contains(keyword) }
        }
    }

    private fun isDairyFree(meal: Meal): Boolean {
        val dairyKeywords = listOf("milk", "cheese", "butter", "cream", "yogurt")
        val ingredients = meal.getIngredient().map { it.first.lowercase() }
        return dairyKeywords.none { keyword ->
            ingredients.any { it.contains(keyword) }
        }
    }

    private fun isLowCarb(meal: Meal): Boolean {
        val highCarbKeywords = listOf("rice", "pasta", "bread", "potato", "noddles", "flour")
        val ingredients = meal.getIngredient().map { it.first.lowercase() }
        return highCarbKeywords.none { keyword ->
            ingredients.any { it.contains(keyword) }
        }
    }

    private fun isKeto(meal: Meal): Boolean {
        return isLowCarb(meal)
    }

    private fun isPaleo(meal: Meal): Boolean {
        val nonPaleoKeywords = listOf(
            "milk", "cheese", "butter", "cream", "beans", "lentils",
            "rice", "pasta", "bread", "flour"
        )
        val ingredients = meal.getIngredient().map { it.first.lowercase() }
        return nonPaleoKeywords.none { keyword ->
            ingredients.any { it.contains(keyword) }
        }
    }

    fun getSearchSuggestions(query: String): Flow<UiState<List<SearchSuggestion>>> = flow {
        emit(UiState.Loading)
        try {
            val suggestions = mutableListOf<SearchSuggestion>()

            getSearchHistory().take(5).forEach { history ->
                if (history.query.contains(query, ignoreCase = true)) {
                    suggestions.add(SearchSuggestion(history.query, SuggestionType.RECENT_SEARCH))
                }
            }

            getTrendingSearches().take(3).forEach { trending ->
                if (trending.contains(query, ignoreCase = true)) {
                    suggestions.add(SearchSuggestion(trending, SuggestionType.TRENDING))
                }
            }

            emit(UiState.Success(suggestions))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Failed to load suggestions"))
        }
    }

    private fun saveSearchHistory(searchHistory: SearchHistory) {
        val currentHistory = getSearchHistory().toMutableList()

        currentHistory.removeAll { it.query == searchHistory.query }
        currentHistory.add(0, searchHistory)

        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeAt(currentHistory.size - 1)
        }

        val json = gson.toJson(currentHistory)
        prefs.edit().putString(KEY_SEARCH_HISTORY, json).apply()
    }

    fun getSearchHistory(): List<SearchHistory> {
        val json = prefs.getString(KEY_SEARCH_HISTORY, null) ?: return emptyList()
        val type = object : TypeToken<List<SearchHistory>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearSearchHistory() {
        prefs.edit().remove(KEY_SEARCH_HISTORY).apply()
    }

    private fun getTrendingSearches(): List<String> {
        return listOf("Chicken", "Pasta", "Vegetarian", "Dessert", "Quick meals", "Healthy")
    }

    fun getAllAreas(): Flow<UiState<List<Area>>> = flow {
        emit(UiState.Loading)
        try {
            val response = apiService.getAllAreas()
            emit(UiState.Success(response.meals))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Failed to load areas"))
        }
    }

    fun getAllIngredients(): Flow<UiState<List<Ingredient>>> = flow {
        emit(UiState.Loading)
        try {
            val response = apiService.getAllIngredients()
            emit(UiState.Success(response.meals))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Failed to load ingredients"))
        }
    }
}