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
                        onFiltersChanged(filters.copy(category = category))
                    }
                )
            }

            // Area Filter
            if (areas is UiState.Success) {
                FilterDropdown(
                    label = "Cuisine",
                    selectedValue = filters.area,
                    options = areas.data.map { it.strArea },
                    onValueSelected = { area ->
                        onFiltersChanged(filters.copy(area = area))
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
                        onFiltersChanged(filters.copy(ingredient = ingredient))
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
                            onFiltersChanged(filters.copy(dietaryRestrictions = newRestrictions))
                        },
                        label = { Text(restriction.displayName) }
                    )
                }
            }
        }
    }
}