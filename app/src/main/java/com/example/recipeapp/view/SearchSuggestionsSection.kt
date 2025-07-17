package com.example.recipeapp.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recipeapp.model.SearchSuggestion
import com.example.recipeapp.model.SuggestionType

@Composable
fun SearchSuggestionsSection(
    suggestions: List<SearchSuggestion>,
    onSuggestionClick: (SearchSuggestion) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            items(suggestions) { suggestion ->
                ListItem(
                    headlineContent = { Text(suggestion.text) },
                    leadingContent = {
                        Icon(
                            when (suggestion.type) {
                                SuggestionType.RECENT_SEARCH -> Icons.Default.ArrowDropDown
                                SuggestionType.TRENDING -> Icons.Default.ArrowDropDown
                                SuggestionType.INGREDIENT -> Icons.Default.ArrowDropDown
                                SuggestionType.CATEGORY -> Icons.Default.ArrowDropDown
                                SuggestionType.AREA -> Icons.Default.ArrowDropDown
                            },
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable { onSuggestionClick(suggestion) }
                )
            }
        }
    }
}