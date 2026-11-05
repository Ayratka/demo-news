package io.github.ayratka.demonews.presentation.categories

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.ayratka.demonews.domain.model.Category
import io.github.ayratka.demonews.presentation.common.components.ErrorBlock

@Composable
fun CategoriesScreen(
    onCategoryClick: (Long) -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    CategoriesContent(
        state = state,
        onRetry = viewModel::retry,
        onCategoryClick = onCategoryClick,
    )
}

@Composable
private fun CategoriesContent(
    state: CategoriesUiState,
    onRetry: () -> Unit,
    onCategoryClick: (Long) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        when (state) {
            CategoriesUiState.Loading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            CategoriesUiState.Empty -> {
                Text("No categories yet", Modifier.align(Alignment.Center))
            }

            is CategoriesUiState.Error -> {
                ErrorBlock(
                    message = state.message,
                    onRetry = onRetry,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            is CategoriesUiState.Success -> {
                CategoriesList(state.categories, onCategoryClick)
            }
        }
    }
}



@Composable
private fun CategoriesList(
    categories: List<Category>,
    onCategoryClick: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(top = 20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        items(categories, key = { it.id }) { category ->
            val onClick = remember(category.id, onCategoryClick) {
                { onCategoryClick(category.id) }
            }
            Text(
                text = category.name.trim(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onClick)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
            )
        }
    }
}


