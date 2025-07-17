package com.example.recipeapp.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recipeapp.model.SearchFilters
import com.example.recipeapp.model.SearchHistory

@Composable
fun SearchHistorySection(
    history: List<SearchHistory>,
    onHistoryClick: (SearchHistory) -> Unit,
    onClearHistory: () -> Unit
) {
    if (history.isEmpty()) return

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Recent Searches",
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = onClearHistory) {
                Text("Clear All")
            }
        }

        LazyColumn(
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            items(history.take(109)) { searchHistory ->
                ListItem(
                    headlineContent = { Text(searchHistory.query) },
                    supportingContent = {
                        if (searchHistory.filters != null && hasActiveFilters(searchHistory.filters)) {
                            Text("With filters")
                        }
                    },
                    leadingContent = {
                        Icon(Icons.Default.Build, contentDescription = null)
                    },
                    modifier = Modifier.clickable { onHistoryClick(searchHistory) }
                )

            }
        }
    }
}

private fun hasActiveFilters(filters: com.example.recipeapp.model.SearchFilters?): Boolean {
    return filters?.let {
        it.category != null ||
                it.area != null ||
                it.ingredient != null ||
                it.dietaryRestrictions.isNotEmpty() ||
                it.maxCookingTime != null ||
                it.difficulty != null
    } ?: false
}