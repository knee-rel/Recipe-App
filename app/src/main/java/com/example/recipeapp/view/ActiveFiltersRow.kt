package com.example.recipeapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recipeapp.model.SearchFilters

@Composable
fun ActiveFiltersRow(
    filters: SearchFilters,
    onRemoveFilter: (String) -> Unit,
    onClearAll: () -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            AssistChip(
                onClick = onClearAll,
                label = { Text("Clear All") },
                leadingIcon = { Icon(Icons.Default.Clear, contentDescription = null) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            )
        }

        filters.category?.let { category ->
            item {
                FilterChip(
                    selected = true,
                    onClick = { onRemoveFilter("category") },
                    label = { Text(category) },
                    trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove") }
                )
            }
        }

        filters.area?.let { area ->
            item {
                FilterChip(
                    selected = true,
                    onClick = { onRemoveFilter("area") },
                    label = { Text(area) },
                    trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove") }
                )
            }
        }

        filters.ingredient?.let { ingredient ->
            item {
                FilterChip(
                    selected = true,
                    onClick = { onRemoveFilter("ingredient") },
                    label = { Text(ingredient) },
                    trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove") }
                )
            }
        }

        filters.dietaryRestrictions.forEach { restriction ->
            item {
                FilterChip(
                    selected = true,
                    onClick = { onRemoveFilter("dietary_${restriction.name}") },
                    label = { Text(restriction.displayName) },
                    trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove") }
                )

            }
        }
    }
}