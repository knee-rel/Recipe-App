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

class MealsViewModel(
    private val repository: RecipeRepository = RecipeRepository()
) : ViewModel() {
    private val _mealsState = MutableStateFlow<UiState<List<Meal>>>(UiState.Loading)
    val mealsState: StateFlow<UiState<List<Meal>>> = _mealsState.asStateFlow()

    fun loadMealsByCategory(category: String) {
        viewModelScope.launch {
            repository.getMealsByCategory(category).collect { state ->
                _mealsState.value = state
            }
        }
    }
}