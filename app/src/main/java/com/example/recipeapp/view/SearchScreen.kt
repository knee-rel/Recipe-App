package com.example.recipeapp.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipeapp.model.DietaryRestriction
import com.example.recipeapp.model.Meal
import com.example.recipeapp.model.SearchFilters
import com.example.recipeapp.model.SearchSuggestion
import com.example.recipeapp.model.UiState
import com.example.recipeapp.viewmodel.SearchViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onMealClick: (String) -> Unit = {},
    searchViewModel: SearchViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by searchViewModel.searchResults.collectAsState()
    val searchSuggestions by searchViewModel.searchSuggestions.collectAsState()
    val searchHistory by searchViewModel.searchHistory.collectAsState()
    val currentFilters by searchViewModel.currentFilters.collectAsState()
    val isFilterExpanded by searchViewModel.isFilterExpanded.collectAsState()
    val areas by searchViewModel.areas.collectAsState()
    val ingredients by searchViewModel.ingredients.collectAsState()
    val categories by searchViewModel.categories.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var showSuggestions by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }

    // Debounced search for suggestions
    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            delay(300) // Debounce delay
            searchViewModel.loadSearchSuggestions(searchQuery)
        } else {
            searchViewModel.clearSearchSuggestions()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newQuery ->
                searchQuery = newQuery
                showSuggestions = newQuery.isNotEmpty()
                isSearchActive = newQuery.isNotEmpty() || hasActiveFilters(currentFilters)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search for meals...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                Row {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            showSuggestions = false
                            isSearchActive = hasActiveFilters(currentFilters)
                            searchViewModel.clearSearch()
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }

                    // Filter toggle button
                    IconButton(onClick = { searchViewModel.toggleFilterExpansion() }) {
                        Icon(
                            if (isFilterExpanded) Icons.Default.Clear else Icons.Default.List,
                            contentDescription = "Toggle Filters",
                            tint = if (hasActiveFilters(currentFilters))
                                MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (searchQuery.isNotBlank() || hasActiveFilters(currentFilters)) {
                        searchViewModel.searchMeals(searchQuery)
                        showSuggestions = false
                        keyboardController?.hide()
                    }
                }
            ),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Active filters row
        if (hasActiveFilters(currentFilters)) {
            ActiveFiltersRow(
                filters = currentFilters,
                onRemoveFilter = { filterType ->
                    removeFilter(searchViewModel, currentFilters, filterType)
                    // Update search active state
                    isSearchActive = searchQuery.isNotEmpty() || hasActiveFilters(currentFilters)
                },
                onClearAll = {
                    searchViewModel.clearFilters()
                    if (searchQuery.isEmpty()) {
                        searchViewModel.clearSearch()
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Filters section
        AnimatedVisibility(
            visible = isFilterExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                FiltersSection(
                    filters = currentFilters,
                    areas = areas,
                    ingredients = ingredients,
                    categories = categories,
                    onFiltersChanged = { newFilters ->
                        searchViewModel.updateFilters(newFilters)
                        isSearchActive = searchQuery.isNotEmpty() || hasActiveFilters(newFilters)
                        if (searchQuery.isNotBlank() || hasActiveFilters(newFilters)) {
                            searchViewModel.searchWithFilters(searchQuery, newFilters)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Search suggestions (only when typing and no results showing)
        if (showSuggestions && searchResults is UiState.Success &&
            (searchResults as UiState.Success<List<Meal>>).data.isEmpty()
        ) {
            when (searchSuggestions) {
                is UiState.Success -> {
                    val suggestions =
                        (searchSuggestions as UiState.Success<List<SearchSuggestion>>).data
                    if (suggestions.isNotEmpty()) {
                        SearchSuggestionsSection(
                            suggestions = suggestions,
                            onSuggestionClick = { suggestion ->
                                searchQuery = suggestion.text
                                searchViewModel.searchMeals(suggestion.text)
                                showSuggestions = false
                                keyboardController?.hide()
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                else -> {}
            }
        }

        // Search History (when no active search and no filters)
        if (!isSearchActive && searchHistory.isNotEmpty()) {
            SearchHistorySection(
                history = searchHistory,
                onHistoryClick = { history ->
                    searchQuery = history.query
                    searchViewModel.searchFromHistory(history)
                    showSuggestions = false
                    isSearchActive = true
                },
                onClearHistory = { searchViewModel.clearSearchHistory() }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Quick Actions (when no search query and no filters)
        if (!isSearchActive) {
            QuickActionsSection(
                onRandomMeal = {
                    searchViewModel.getRandomMeal()
                    isSearchActive = true
                },
                onCategoryClick = { category ->
                    searchViewModel.searchByCategory(category)
                    isSearchActive = true
                },
                categories = categories
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Search Results
        when (searchResults) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Success -> {
                val meals = (searchResults as UiState.Success<List<Meal>>).data
                if (meals.isEmpty() && isSearchActive) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No meals found",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                "Try adjusting your search or filters",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else if (meals.isNotEmpty()) {
                    LazyColumn {
                        item {
                            Text(
                                text = "${meals.size} recipe${if (meals.size != 1) "s" else ""} found",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(meals) { meal ->
                            MealItem(
                                meal = meal,
                                onClick = { onMealClick(meal.idMeal) }
                            )
                        }
                    }
                }
            }

            is UiState.Error -> {
                ErrorMessage(
                    message = (searchResults as UiState.Error).message,
                    onRetry = {
                        if (searchQuery.isNotBlank()) {
                            searchViewModel.searchMeals(searchQuery)
                        }
                    }
                )
            }
        }
    }
}

fun hasActiveFilters(filters: SearchFilters): Boolean {
    return filters.category != null ||
            filters.area != null ||
            filters.ingredient != null ||
            filters.dietaryRestrictions.isNotEmpty() ||
            filters.maxCookingTime != null ||
            filters.difficulty != null
}

fun removeFilter(viewModel: SearchViewModel, currentFilters: SearchFilters, filterType: String) {
    val newFilters = when {
        filterType == "category" -> currentFilters.copy(category = null)
        filterType == "area" -> currentFilters.copy(area = null)
        filterType == "ingredient" -> currentFilters.copy(ingredient = null)
        filterType.startsWith("dietary_") -> {
            val restrictionName = filterType.removePrefix("dietary_")
            val restriction = DietaryRestriction.values().find { it.name == restrictionName }
            if (restriction != null) {
                currentFilters.copy(dietaryRestrictions = currentFilters.dietaryRestrictions - restriction)
            } else currentFilters
        }

        else -> currentFilters
    }
    viewModel.updateFilters(newFilters)
}