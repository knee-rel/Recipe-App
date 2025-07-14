package com.example.recipeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.manager.FavoritesManager
import com.example.recipeapp.model.Meal
import com.example.recipeapp.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val favoritesManager = FavoritesManager(application)

    private val _favoritesState = MutableStateFlow<UiState<List<Meal>>>(UiState.Loading)
    val favoritesState: StateFlow<UiState<List<Meal>>> = _favoritesState

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    init {

    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val favorites = favoritesManager.getFavorites()
                _favoritesState.value = UiState.Success(favorites)
            } catch (e: Exception) {
                _favoritesState.value = UiState.Error("Failed to load favorites")
            }
        }
    }

    fun addToFavorites(meal: Meal) {
        viewModelScope.launch {
            favoritesManager.addToFavorites(meal)
            loadFavorites()
            _isFavorite.value = true
        }
    }

    fun removeFromFavorites(mealId: String) {
        viewModelScope.launch {
            favoritesManager.removeFromFavorites(mealId)
            loadFavorites()
            _isFavorite.value = false
        }
    }

    fun checkIfFavorite(mealId: String) {
        _isFavorite.value = favoritesManager.isFavorite(mealId)
    }

    fun clearAllFavorites() {
        viewModelScope.launch {
            favoritesManager.clearAllFavorites()
            loadFavorites()
        }
    }
}