package com.example.recipeapp.respository

import com.example.recipeapp.model.Area
import com.example.recipeapp.model.Category
import com.example.recipeapp.model.Ingredient
import com.example.recipeapp.model.Meal
import com.example.recipeapp.model.UiState
import com.example.recipeapp.service.NetworkModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RecipeRepository {
    private val apiService = NetworkModule.apiService

    fun getCategories(): Flow<UiState<List<Category>>> = flow {
        emit(UiState.Loading)
        try {
            val response = apiService.getCategories()
            emit(UiState.Success(response.categories))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getMealsByCategory(category: String): Flow<UiState<List<Meal>>> = flow {
        emit(UiState.Loading)
        try {
            val response = apiService.getMealsByCategory(category)
            emit(UiState.Success(response.meals ?: emptyList()))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun searchMealsByName(letter: String): Flow<UiState<List<Meal>>> = flow {
        emit(UiState.Loading)
        try {
            val response = apiService.searchMealsByFirstLetter(letter)
            emit(UiState.Success(response.meals ?: emptyList()))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun searchMealsByFirstLetter(letter: String): Flow<UiState<List<Meal>>> = flow {
        emit(UiState.Loading)
        try {
            val response = apiService.searchMealsByFirstLetter(letter)
            emit(UiState.Success(response.meals ?: emptyList()))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getMealById(id: String): Flow<UiState<Meal>> = flow {
        emit(UiState.Loading)
        try {
            val response = apiService.getMealById(id)
            val meal = response.meals?.firstOrNull()
            if (meal != null) {
                emit(UiState.Success(meal))
            } else {
                emit(UiState.Error("Meal not found"))
            }
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getRandomMeal(): Flow<UiState<List<Meal>>> = flow {
        emit(UiState.Loading)
        try {
            val response = apiService.getRandomMeal()
            emit(UiState.Success(response.meals))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getMealsByIngredient(ingredient: String): Flow<UiState<List<Meal>>> = flow {
        emit(UiState.Loading)
        try {
            val response = apiService.getMealsByIngredient(ingredient)
            emit(UiState.Success(response.meals ?: emptyList()))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getMealsByArea(area: String): Flow<UiState<List<Meal>>> = flow {
        emit(UiState.Loading)
        try {
            val response = apiService.getMealsByArea(area)
            emit(UiState.Success(response.meals ?: emptyList()))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getAllAreas(): Flow<UiState<List<Area>>> = flow {
        emit(UiState.Loading)
        try {
            val response = apiService.getAllAreas()
            emit(UiState.Success(response.meals))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getAllIngredients(): Flow<UiState<List<Ingredient>>> = flow {
        emit(UiState.Loading)
        try {
            val response = apiService.getAllIngredients()
            emit(UiState.Success(response.meals))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Unknown error occurred"))
        }
    }
}