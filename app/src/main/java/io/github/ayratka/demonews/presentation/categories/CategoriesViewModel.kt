package io.github.ayratka.demonews.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.ayratka.demonews.domain.usecases.GetCategories
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val getCategories: GetCategories,
) : ViewModel() {

    private val retryTrigger = MutableSharedFlow<Unit>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private val _uiState: MutableStateFlow<CategoriesUiState> =
        MutableStateFlow(CategoriesUiState.Empty)

    val uiState: StateFlow<CategoriesUiState> = retryTrigger
        .flatMapLatest { loadCategories() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CategoriesUiState.Loading,
        )

    init {
        retryTrigger.tryEmit(Unit)
    }

    private fun loadCategories(): Flow<CategoriesUiState> {
        val source = flow {
            val categories = getCategories()
            emit(
                if (categories.isEmpty()) CategoriesUiState.Empty
                else CategoriesUiState.Success(categories)
            )
        }
        return source
            .onStart { emit(CategoriesUiState.Loading) }
            .catch { emit(CategoriesUiState.Error(it.message ?: "Failed to load categories")) }
    }

    fun retry(){
        retryTrigger.tryEmit(Unit)
    }

}