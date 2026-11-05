package io.github.ayratka.demonews.presentation.newslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.ayratka.demonews.domain.model.News
import io.github.ayratka.demonews.presentation.common.components.ErrorBlock
import io.github.ayratka.demonews.presentation.common.components.FooterError
import io.github.ayratka.demonews.presentation.common.components.FooterLoader
import io.github.ayratka.demonews.presentation.common.formatters.toDisplayDate

private const val PREFETCH_THRESHOLD = 3

@Composable
fun NewsListScreen(
    onNewsClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    val shouldLoadMore by remember(listState) {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf false
            val total = listState.layoutInfo.totalItemsCount
            total > 0 && last >= total - PREFETCH_THRESHOLD
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadMore()
    }

    NewsListContent(
        state = state,
        listState = listState,
        onRetry = viewModel::retry,
        onNewsClick = onNewsClick,
        modifier = modifier,
    )
}

@Composable
private fun NewsListContent(
    state: NewsListUiState,
    listState: LazyListState,
    onRetry: () -> Unit,
    onNewsClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            state.isInitialLoading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            state.initialError != null -> {
                ErrorBlock(
                    message = state.initialError!!,
                    onRetry = onRetry,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            state.isEmpty -> {
                Text(
                    text = "No news yet",
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            else -> {
                NewsList(
                    state = state,
                    listState = listState,
                    onRetry = onRetry,
                    onNewsClick = onNewsClick,
                )
            }
        }
    }
}

@Composable
private fun NewsList(
    state: NewsListUiState,
    listState: LazyListState,
    onRetry: () -> Unit,
    onNewsClick: (Long) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(state.items, key = { it.id }) { news ->
            NewsCard(
                news = news,
                onClick = { onNewsClick(news.id) },
            )
        }

        when {
            state.isLoadingMore -> item(key = "footer_loading") {
                FooterLoader()
            }
            state.appendError != null -> item(key = "footer_error") {
                FooterError(
                    message = state.appendError!!,
                    onRetry = onRetry,
                )
            }
        }
    }
}

@Composable
private fun NewsCard(
    news: News,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = news.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            news.shortDescription?.takeIf { it.isNotBlank() }?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                text = news.date.toDisplayDate(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}