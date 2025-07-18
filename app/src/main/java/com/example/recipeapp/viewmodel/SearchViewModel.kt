// SearchViewModel.kt
package com.example.recipeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.Screen
import com.example.recipeapp.model.Area
import com.example.recipeapp.model.Category
import com.example.recipeapp.model.DietaryRestriction
import com.example.recipeapp.model.Ingredient
import com.example.recipeapp.model.Meal
import com.example.recipeapp.model.SearchFilters
import com.example.recipeapp.model.SearchHistory
import com.example.recipeapp.model.SearchSuggestion
import com.example.recipeapp.model.UiState
import com.example.recipeapp.respository.RecipeRepository
import com.example.recipeapp.respository.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val searchRepository = SearchRepository(application)
    private val recipeRepository = RecipeRepository()

    private val _searchResults = MutableStateFlow<UiState<List<Meal>>>(UiState.Success(emptyList()))
    val searchResults: StateFlow<UiState<List<Meal>>> = _searchResults.asStateFlow()

    private val _searchSuggestions =
        MutableStateFlow<UiState<List<SearchSuggestion>>>(UiState.Success(emptyList()))
    val searchSuggestions: StateFlow<UiState<List<SearchSuggestion>>> =
        _searchSuggestions.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<SearchHistory>>(emptyList())
    val searchHistory: StateFlow<List<SearchHistory>> = _searchHistory.asStateFlow()

    private val _currentFilters = MutableStateFlow(SearchFilters())
    val currentFilters: StateFlow<SearchFilters> = _currentFilters.asStateFlow()

    private val _areas = MutableStateFlow<UiState<List<Area>>>(UiState.Loading)
    val areas: StateFlow<UiState<List<Area>>> = _areas.asStateFlow()

    private val _ingredients = MutableStateFlow<UiState<List<Ingredient>>>(UiState.Loading)
    val ingredients: StateFlow<UiState<List<Ingredient>>> = _ingredients.asStateFlow()

    private val _categories = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val categories: StateFlow<UiState<List<Category>>> = _categories.asStateFlow()

    private val _isFilterExpanded = MutableStateFlow(false)
    val isFilterExpanded: StateFlow<Boolean> = _isFilterExpanded.asStateFlow()

    init {
        loadFilterOptions()
        loadSearchHistory()
    }

    private fun loadFilterOptions() {
        viewModelScope.launch {
            searchRepository.getAllAreas().collect { state ->
                _areas.value = state
            }
        }

        viewModelScope.launch {
            searchRepository.getAllIngredients().collect { state ->
                _ingredients.value = state
            }
        }

        viewModelScope.launch {
            recipeRepository.getCategories().collect { state ->
                _categories.value = state
            }
        }
    }

    private fun loadSearchHistory() {
        _searchHistory.value = searchRepository.getSearchHistory()
    }

    fun searchWithFilters(query: String, filters: SearchFilters = _currentFilters.value) {
        viewModelScope.launch {
            searchRepository.searchWithFilters(query, filters).collect { state ->
                _searchResults.value = state

                if (state is UiState.Success && query.isNotBlank()) {
                    loadSearchHistory()
                }
            }
        }
    }

    fun searchMeals(query: String) {
        searchWithFilters(query, _currentFilters.value)
    }

    fun updateFilters(filters: SearchFilters) {
        println("⚫ SearchViewModel - updateFilters CALLED")
        println("   Received filters: $filters")
        println("   Current _currentFilters.value BEFORE: ${_currentFilters.value}")

        try {
            _currentFilters.value = filters

            val newValue = _currentFilters.value
            println("Current _currentFilters.value AFTER: $newValue")

            if (newValue == filters) {
                println("   ✅ Update SUCCESS: Values match")
            } else {
                println("   ❌ Update FAILED: Values don't match")
                println("   Expected: $filters")
                println("   Actual: $newValue")

                // ✅ TRY alternative update method
                println("   Trying alternative update method...")
                _currentFilters.tryEmit(filters)
                println("   After tryEmit: ${_currentFilters.value}")
            }
        } catch (e: Exception) {
            println("   ❌ Exception during update: ${e.message}")
            e.printStackTrace()
        }
    }

    fun clearFilters() {
        _currentFilters.value = SearchFilters()

        if (_searchResults.value is UiState.Success) {
            val currentResults = (_searchResults.value as UiState.Success<List<Meal>>).data
            if (currentResults.isNotEmpty()) {
                _searchResults.value = UiState.Success(emptyList())
            }
        }
    }

    fun toggleFilterExpansion() {
        _isFilterExpanded.value = !_isFilterExpanded.value
    }

    fun loadSearchSuggestions(query: String) {
        if (query.length < 2) {
            _searchSuggestions.value = UiState.Success(emptyList())
            return
        }

        viewModelScope.launch {
            searchRepository.getSearchSuggestions(query).collect { state ->
                _searchSuggestions.value = state
            }
        }
    }

    fun clearSearchSuggestions() {
        _searchSuggestions.value = UiState.Success(emptyList())
    }

    fun clearSearchHistory() {
        searchRepository.clearSearchHistory()
        loadSearchHistory()
    }

    fun clearSearch() {
        _searchResults.value = UiState.Success(emptyList())
        _searchSuggestions.value = UiState.Success(emptyList())
    }

    fun searchFromHistory(searchHistory: SearchHistory) {
        searchHistory.filters?.let { filters ->
            _currentFilters.value = filters
        }
        searchWithFilters(searchHistory.query, _currentFilters.value)
    }

    fun searchByCategory(category: String) {
        val filters = _currentFilters.value.copy(category = category)
        updateFilters(filters)
        searchWithFilters("", filters)
    }

    fun searchByArea(area: String) {
        val filters = _currentFilters.value.copy(area = area)
        updateFilters(filters)
        searchWithFilters("", filters)
    }

    fun searchByIngredient(ingredient: String) {
        val filters = _currentFilters.value.copy(ingredient = ingredient)
        updateFilters(filters)
        searchWithFilters("", filters)
    }

    fun addDietaryRestriction(restriction: DietaryRestriction) {
        val currentRestrictions = _currentFilters.value.dietaryRestrictions.toMutableList()
        if (!currentRestrictions.contains(restriction)) {
            currentRestrictions.add(restriction)
            val updatedFilters =
                _currentFilters.value.copy(dietaryRestrictions = currentRestrictions)
            updateFilters(updatedFilters)
        }
    }

    fun removeDietaryRestriction(restriction: DietaryRestriction) {
        val currentRestrictions = _currentFilters.value.dietaryRestrictions.toMutableList()
        currentRestrictions.remove(restriction)
        val updatedFilters = _currentFilters.value.copy(dietaryRestrictions = currentRestrictions)
        updateFilters(updatedFilters)
    }

    fun getRandomMeal() {
        viewModelScope.launch {
            recipeRepository.getRandomMeal().collect { state ->
                _searchResults.value = state
            }
        }
    }
}