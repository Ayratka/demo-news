package io.github.ayratka.demonews.presentation.common.pagination

data class PagingState<Key, Item>(
    val currentKey: Key,
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val isEndReached: Boolean = false,
    val error: String? = null
){
    val isInitialLoading: Boolean
        get() = isLoading && items.isEmpty()

    val isLoadingMore: Boolean
        get() = isLoading && items.isNotEmpty()

    val initialError: String?
        get() = error?.takeIf { items.isEmpty() }

    val appendError: String?
        get() = error?.takeIf { items.isNotEmpty() }

    val isEmpty: Boolean
        get() = items.isEmpty() && !isLoading && error == null
}