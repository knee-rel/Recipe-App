package com.example.recipeapp.view

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import com.example.recipeapp.model.Category

@Composable
fun CategoryScreen(
    categories: List<Category>,
    onCategoryClick: (String) -> Unit = {}
) {
    LazyVerticalGrid(GridCells.Fixed(2)) {
        items(categories) { category ->
            CategoryItem(category = category, onClick = {
                onCategoryClick(category.strCategory)
            })
        }
    }
}