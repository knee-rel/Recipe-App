package com.example.recipeapp.manager

import android.content.Context
import android.content.SharedPreferences
import com.example.recipeapp.model.Meal
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class FavoritesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun addToFavorites(meal: Meal) {
        val favorites = getFavorites().toMutableList()
        if (!favorites.any { it.idMeal == meal.idMeal }) {
            favorites.add(meal)
            saveFavorites(favorites)
        }
    }

    fun removeFromFavorites(mealId: String) {
        val favorites = getFavorites().toMutableList()
        favorites.removeAll { it.idMeal == mealId }
        saveFavorites(favorites)
    }

    fun getFavorites(): List<Meal> {
        val favoritesJson = sharedPreferences.getString("favorites_list", "[]")
        val type = object : TypeToken<List<Meal>>() {}.type
        return gson.fromJson(favoritesJson, type) ?: emptyList()
    }

    fun isFavorite(mealId: String): Boolean {
        return getFavorites().any { it.idMeal == mealId }
    }

    private fun saveFavorites(favorites: List<Meal>) {
        val favoritesJson = gson.toJson(favorites)
        sharedPreferences.edit()
            .putString("favorites_list", favoritesJson)
            .apply()
    }

    fun clearAllFavorites() {
        sharedPreferences.edit()
            .remove("favorites_list")
            .apply()
    }
}