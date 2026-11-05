package io.github.ayratka.demonews.presentation.newsdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ayratka.demonews.domain.model.Outcome
import io.github.ayratka.demonews.domain.usecases.GetNewsDetails
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NewsDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNewsDetails: GetNewsDetails,
) : ViewModel() {

    private val newsId: Long = checkNotNull(savedStateHandle["newsId"])

    private val retryTrigger = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val _uiState: MutableStateFlow<NewsDetailsUiState> =
        MutableStateFlow(NewsDetailsUiState.Empty)

    init {
        retryTrigger.tryEmit(Unit)
    }


    val uiState: StateFlow<NewsDetailsUiState> = retryTrigger
        .flatMapLatest { loadDetails() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NewsDetailsUiState.Loading,
        )

    fun retry() {
        retryTrigger.tryEmit(Unit)
    }

    private fun loadDetails(): Flow<NewsDetailsUiState> = flow {
        when (val result = getNewsDetails(newsId)) {
            is Outcome.Success -> emit(NewsDetailsUiState.Success(result.value))
            is Outcome.Failure -> emit(NewsDetailsUiState.Error(result.error.message))
        }
    }
        .onStart { emit(NewsDetailsUiState.Loading) }
}