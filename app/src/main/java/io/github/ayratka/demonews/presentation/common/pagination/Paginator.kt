package io.github.ayratka.demonews.presentation.common.pagination

import io.github.ayratka.demonews.domain.model.Outcome

object Paginator {

    suspend fun <Key, Item> loadNext(
        currentState: PagingState<Key, Item>,
        onRequest: suspend (nextKey: Key) -> Outcome<List<Item>>,
        getNextKey: (List<Item>, Key) -> Key,
    ): PagingState<Key, Item> {

        if (currentState.isLoading || currentState.isEndReached) return currentState

        val loadingState = currentState.copy(isLoading = true, error = null)

        return when (val result = onRequest(currentState.currentKey)) {
            is Outcome.Success -> loadingState.copy(
                items = currentState.items + result.value,
                currentKey = getNextKey(result.value, currentState.currentKey),
                isLoading = false,
                isEndReached = result.value.isEmpty(),
            )
            is Outcome.Failure -> loadingState.copy(
                isLoading = false,
                error = result.error.message,
            )
        }
    }
}