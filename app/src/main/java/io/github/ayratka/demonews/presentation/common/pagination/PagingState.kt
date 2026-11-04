package io.github.ayratka.demonews.presentation.common.pagination

data class PagingState<Key, Item>(
    val currentKey: Key,
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val isEndReached: Boolean = false,
    val error: Throwable? = null
)