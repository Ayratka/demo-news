package io.github.ayratka.demonews.presentation.common.pagination

object Paginator {

    suspend inline fun <Key, Item> loadNext(
        currentState: PagingState<Key, Item>,
        onRequest: (nextKey: Key) -> Result<List<Item>>,
        getNextKey: (List<Item>, Key) -> Key
    ): PagingState<Key, Item> {

        if (currentState.isLoading || currentState.isEndReached) return currentState

        val loadingState = currentState.copy(isLoading = true, error = null)

        return onRequest(currentState.currentKey)
            .fold(
                onSuccess = { newItems ->
                    loadingState.copy(
                        items = currentState.items + newItems,
                        currentKey = getNextKey(newItems, currentState.currentKey),
                        isLoading = false,
                        isEndReached = newItems.isEmpty()
                    )
                },
                onFailure = { throwable ->
                    loadingState.copy(
                        isLoading = false,
                        error = throwable
                    )
                }
            )
    }
}