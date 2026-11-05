package io.github.ayratka.demonews.presentation.newsdetails

import io.github.ayratka.demonews.domain.model.NewsDetails

sealed interface NewsDetailsUiState {
    data object Loading : NewsDetailsUiState

    data object Empty : NewsDetailsUiState
    data class Success(val details: NewsDetails) : NewsDetailsUiState
    data class Error(val message: String) : NewsDetailsUiState
}