package com.example.recipeapp.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.recipeapp.model.Category
import com.example.recipeapp.model.UiState
import com.example.recipeapp.viewmodel.CategoriesViewModel

@Composable
fun RecipeScreen(modifier: Modifier = Modifier, onCategoryClick: (String) -> Unit = {}) {
    val categoriesViewModel: CategoriesViewModel = viewModel()
    val categoriesState by categoriesViewModel.categoriesState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (categoriesState) {
            is UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is UiState.Success -> {
                CategoryScreen(
                    categories = (categoriesState as UiState.Success<List<Category>>).data,
                    onCategoryClick = onCategoryClick
                )
            }

            is UiState.Error -> {
                ErrorMessage(
                    message = (categoriesState as UiState.Error).message,
                    onRetry = categoriesViewModel::retry,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error: $message",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}