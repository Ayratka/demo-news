package io.github.ayratka.demonews.presentation.categories

import io.github.ayratka.demonews.domain.model.Category

sealed interface CategoriesUiState {
    data object Loading : CategoriesUiState
    data object Empty : CategoriesUiState
    data class Success(val categories: List<Category>) : CategoriesUiState
    data class Error(val message: String) : CategoriesUiState
}