package com.example.recipeapp.service

import com.example.recipeapp.model.AreasResponse
import com.example.recipeapp.model.CategoriesResponse
import com.example.recipeapp.model.IngredientsResponse
import com.example.recipeapp.model.MealsResponse
import com.example.recipeapp.model.RandomMealResponse
import com.example.recipeapp.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("categories.php")
    suspend fun getCategories(): CategoriesResponse

    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): MealsResponse

    @GET("search.php")
    suspend fun searchMealsByName(@Query("s") name: String): SearchResponse

    @GET("search.php")
    suspend fun searchMealsByFirstLetter(@Query("s") name: String): SearchResponse

    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String): SearchResponse

    @GET("random.php")
    suspend fun getRandomMeal(): RandomMealResponse

    @GET("filter.php")
    suspend fun getMealsByIngredient(@Query("i") ingredient: String): MealsResponse

    @GET("filter.php")
    suspend fun getMealsByArea(@Query("a") area: String): MealsResponse

    @GET("list.php?c=list")
    suspend fun getAllCategories(): CategoriesResponse

    @GET("list.php?a=list")
    suspend fun getAllAreas(): AreasResponse

    @GET("list.php?i=list")
    suspend fun getAllIngredients(): IngredientsResponse
}