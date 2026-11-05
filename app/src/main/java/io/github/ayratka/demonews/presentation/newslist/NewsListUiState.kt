package io.github.ayratka.demonews.presentation.newslist

import io.github.ayratka.demonews.domain.model.News
import io.github.ayratka.demonews.presentation.common.pagination.PagingState

typealias NewsListUiState = PagingState<Int, News>
