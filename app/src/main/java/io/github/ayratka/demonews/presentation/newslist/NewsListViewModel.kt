package io.github.ayratka.demonews.presentation.newslist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ayratka.demonews.domain.model.News
import io.github.ayratka.demonews.domain.usecases.GetNewsByCategory
import io.github.ayratka.demonews.presentation.common.pagination.Paginator
import io.github.ayratka.demonews.presentation.common.pagination.PagingState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNewsByCategory: GetNewsByCategory,
) : ViewModel() {

    private val categoryId: Long = checkNotNull(savedStateHandle["categoryId"])

    private sealed interface Command {
        data object LoadMore : Command
        data object Refresh : Command
    }

    private val commands = MutableSharedFlow<Command>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val _state = MutableStateFlow(PagingState<Int, News>(currentKey = STARTING_PAGE))
    val state: StateFlow<PagingState<Int, News>> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            commands
                .onStart { emit(Command.LoadMore) }
                .collectLatest { reduce(it) }
        }
    }

    fun loadMore() = commands.tryEmit(Command.LoadMore)

    fun refresh() = commands.tryEmit(Command.Refresh)

    fun retry() {
        val snapshot = _state.value
        when {
            snapshot.appendError != null -> {
                _state.value = snapshot.copy(error = null)
                commands.tryEmit(Command.LoadMore)
            }
            snapshot.initialError != null -> commands.tryEmit(Command.Refresh)
        }
    }

    private suspend fun reduce(command: Command) {
        val base = when (command) {
            Command.Refresh -> PagingState(currentKey = STARTING_PAGE)
            Command.LoadMore -> _state.value
        }
        _state.value = Paginator.loadNext(
            currentState = base,
            onRequest = { page -> getNewsByCategory(categoryId, page) },
            getNextKey = { _, page -> page + 1 },
        )
    }

    companion object {
        private const val STARTING_PAGE = 0
    }
}