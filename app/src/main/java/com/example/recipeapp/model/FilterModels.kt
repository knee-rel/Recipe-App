package com.example.recipeapp.model

data class SearchFilters(
    val category: String? = null,
    val area: String? = null,
    val ingredient: String? = null,
    val dietaryRestrictions: List<DietaryRestriction> = emptyList(),
    val maxCookingTime: Int? = null,
    val difficulty: DifficultyLevel? = null,
)

enum class DietaryRestriction(val displayName: String) {
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    GLUTEN_FREE("Gluten Free"),
    DAIRY_FREE("Dairy Free"),
    LOW_CARB("Low Carb"),
    KETO("Keto"),
    PALEO("Paleo")
}

enum class DifficultyLevel(val displayName: String, val value: Int) {
    EASY("Easy", 1),
    MEDIUM("Medium", 2),
    HARD("Hard", 3)
}

data class SearchSuggestion(
    val text: String,
    val type: SuggestionType,
    val count: Int = 0
)

enum class SuggestionType {
    RECENT_SEARCH,
    TRENDING,
    INGREDIENT,
    CATEGORY,
    AREA
}

data class SearchHistory(
    val id: Long = 0,
    val query: String,
    val timestamp: Long = System.currentTimeMillis(),
    val filters: SearchFilters? = null,
)