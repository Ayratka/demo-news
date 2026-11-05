package io.github.ayratka.demonews.presentation.newsdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.ayratka.demonews.domain.model.NewsDetails
import io.github.ayratka.demonews.presentation.common.components.ErrorBlock
import io.github.ayratka.demonews.presentation.common.components.HtmlText
import io.github.ayratka.demonews.presentation.common.formatters.toDisplayDate

@Composable
fun NewsDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: NewsDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    NewsDetailsContent(
        state = state,
        onRetry = viewModel::retry,
        modifier = modifier,
    )
}

@Composable
private fun NewsDetailsContent(
    state: NewsDetailsUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (state) {
            NewsDetailsUiState.Loading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            is NewsDetailsUiState.Error -> {
                ErrorBlock(
                    message = state.message,
                    onRetry = onRetry,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            is NewsDetailsUiState.Success -> {
                DetailsBody(state.details)
            }

            NewsDetailsUiState.Empty -> {
                Text("No news details yet", Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun DetailsBody(details: NewsDetails) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = details.title,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = details.date.toDisplayDate(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = details.shortDescription,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        HtmlText(
            html = details.fullDescription,
        )
    }
}