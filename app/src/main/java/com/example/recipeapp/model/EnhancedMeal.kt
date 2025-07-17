package com.example.recipeapp.model

import androidx.compose.ui.layout.ScaleFactor

data class EnhancedMeal(
    val baseMeal: Meal,
    val servings: Int = 4,
    val prepTime: Int? = null,
    val cookTime: Int? = null,
    val difficulty: DifficultyLevel = DifficultyLevel.MEDIUM,
    val nutrition: NutritionInfo? = null,
    val dietaryTags: List<DietaryRestriction> = emptyList(),
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val isBookmarked: Boolean = false
) {
    val totalTime: Int?
        get() = if (prepTime != null && cookTime != null) prepTime + cookTime else null

    fun getScaledIngredients(targetServings: Int): List<Pair<String, String>> {
        val scaleFactor = targetServings.toFloat() / servings
        return baseMeal.getIngredient().map { (ingredient, measure) ->
            ingredient to scaleQuantity(measure, scaleFactor)
        }
    }

    private fun scaleQuantity(measure: String, scaleFactor: Float): String {
        if (measure.isBlank()) return measure

        val numberRegex = "\\d+(\\.\\d+)?".toRegex()
        val matchResult = numberRegex.find(measure)

        return if (matchResult != null) {
            val originalAmount = matchResult.value.toFloatOrNull()
            if (originalAmount != null) {
                val scaledAmount = originalAmount * scaleFactor
                val formattedAmount = if (scaledAmount % 1 == 0f) {
                    scaledAmount.toInt().toString()
                } else {
                    String.format("%.1f", scaledAmount)
                }
                measure.replace(matchResult.value, formattedAmount)
            } else measure
        } else measure
    }
}

data class NutritionInfo(
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
    val fiber: Float? = null,
    val sugar: Float? = null,
    val sodium: Int? = null,
)

fun Meal.toEnhanced(
    servings: Int = 4,
    prepTime: Int? = null,
    cookTime: Int? = null,
    difficulty: DifficultyLevel = DifficultyLevel.MEDIUM,
    nutrition: NutritionInfo? = null,
    dietaryTags: List<DietaryRestriction> = emptyList(),
    rating: Float = 0f,
    reviewCount: Int = 0,
    isBookmarked: Boolean = false
) = EnhancedMeal(
    baseMeal = this,
    servings = servings,
    prepTime = prepTime,
    cookTime = cookTime,
    difficulty = difficulty,
    nutrition = nutrition,
    dietaryTags = dietaryTags,
    rating = rating,
    reviewCount = reviewCount,
    isBookmarked = isBookmarked
)

