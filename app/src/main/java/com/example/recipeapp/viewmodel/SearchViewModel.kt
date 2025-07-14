// SearchViewModel.kt
package com.example.recipeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.model.Meal
import com.example.recipeapp.model.UiState
import com.example.recipeapp.respository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: RecipeRepository = RecipeRepository()
) : ViewModel() {

    private val _searchResults = MutableStateFlow<UiState<List<Meal>>>(UiState.Success(emptyList()))
    val searchResults: StateFlow<UiState<List<Meal>>> = _searchResults.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    fun searchMeals(query: String) {
        if (query.isBlank()) return

        addToRecentSearches(query)

        viewModelScope.launch {
            repository.searchMealsByName(query).collect { state ->
                _searchResults.value = state
            }
        }
    }

    fun searchByFirstLetter(letter: String) {
        viewModelScope.launch {
            repository.searchMealsByFirstLetter(letter).collect { state ->
                _searchResults.value = state
            }
        }
    }

    fun getRandomMeal() {
        viewModelScope.launch {
            repository.getRandomMeal().collect { state ->
                _searchResults.value = state
            }
        }
    }

    fun clearSearch() {
        _searchResults.value = UiState.Success(emptyList())
    }

    private fun addToRecentSearches(query: String) {
        val currentSearches = _recentSearches.value.toMutableList()
        if (currentSearches.contains(query)) {
            currentSearches.remove(query)
        }
        currentSearches.add(0, query)
        if (currentSearches.size > 10) {
            currentSearches.removeAt(currentSearches.size - 1)
        }
        _recentSearches.value = currentSearches
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }
}