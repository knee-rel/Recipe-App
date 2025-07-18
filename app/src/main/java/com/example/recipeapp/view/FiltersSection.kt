package com.example.recipeapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.recipeapp.model.Area
import com.example.recipeapp.model.Category
import com.example.recipeapp.model.DietaryRestriction
import com.example.recipeapp.model.Ingredient
import com.example.recipeapp.model.SearchFilters
import com.example.recipeapp.model.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersSection(
    filters: SearchFilters,
    areas: UiState<List<Area>>,
    ingredients: UiState<List<Ingredient>>,
    categories: UiState<List<Category>>,
    onFiltersChanged: (SearchFilters) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Filters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Category Filter
            if (categories is UiState.Success) {
                FilterDropdown(
                    label = "Category",
                    selectedValue = filters.category,
                    options = categories.data.map { it.strCategory },
                    onValueSelected = { category ->
//                        onFiltersChanged(filters.copy(category = category))
                        val newFilters = filters.copy(category = category)
                        onFiltersChanged(newFilters)
                    }
                )
            }

            // Area Filter
            if (areas is UiState.Success) {
                println("🟡 FiltersSection - Rendering Area dropdown")
                println("   Current filters.area: '${filters.area}'")
                println("   Available areas: ${areas.data.take(3).map { it.strArea }}")

                FilterDropdown(
                    label = "Cuisine",
                    selectedValue = filters.area,
                    options = areas.data.map { it.strArea },
                    onValueSelected = { area ->
                        println("🟠 FiltersSection - Area callback RECEIVED: '$area'")
                        val newFilters = filters.copy(area = area)
                        println("   Created newFilters: $newFilters")
                        println("   About to call onFiltersChanged...")
                        onFiltersChanged(newFilters)
                        println("   onFiltersChanged called successfully")
                    }
                )
            }

            // Ingredient Filter
            if (ingredients is UiState.Success) {
                FilterDropdown(
                    label = "Main Ingredient",
                    selectedValue = filters.ingredient,
                    options = ingredients.data.take(20)
                        .map { it.strIngredient }, // Limit for performance
                    onValueSelected = { ingredient ->
//                        onFiltersChanged(filters.copy(ingredient = ingredient))
                        val newFilters = filters.copy(ingredient = ingredient)
                        onFiltersChanged(newFilters)
                    }
                )
            }

            // Dietary Restrictions
            Text(
                "Dietary Preferences",
                style = MaterialTheme.typography.labelLarge
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(DietaryRestriction.values()) { restriction ->
                    FilterChip(
                        selected = filters.dietaryRestrictions.contains(restriction),
                        onClick = {
                            val newRestrictions =
                                if (filters.dietaryRestrictions.contains(restriction)) {
                                    filters.dietaryRestrictions - restriction
                                } else {
                                    filters.dietaryRestrictions + restriction
                                }
//                            onFiltersChanged(filters.copy(dietaryRestrictions = newRestrictions))
                            val newFilters = filters.copy(dietaryRestrictions = newRestrictions)
                            onFiltersChanged(newFilters)
                        },
                        label = { Text(restriction.displayName) }
                    )
                }
            }
        }
    }
}