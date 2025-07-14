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

class MealDetailViewModel(
    private val repository: RecipeRepository = RecipeRepository()
) : ViewModel() {

    private val _mealState = MutableStateFlow<UiState<Meal>>(UiState.Loading)
    val mealState: StateFlow<UiState<Meal>> = _mealState.asStateFlow()

    fun loadMealDetails(mealId: String) {
        viewModelScope.launch {
            repository.getMealById(mealId).collect { state ->
                _mealState.value = state
            }
        }
    }
}