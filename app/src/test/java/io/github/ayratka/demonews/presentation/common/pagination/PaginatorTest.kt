package io.github.ayratka.demonews.presentation.common.pagination

import io.github.ayratka.demonews.domain.model.Outcome
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.coroutines.cancellation.CancellationException

class PaginatorTest {

    private val initial = PagingState<Int, String>(currentKey = 1)

    @Test
    fun `appends items and advances key on success`() = runTest {
        val result = Paginator.loadNext(
            currentState = initial,
            onRequest = { Outcome.Success(listOf("a", "b")) },
            getNextKey = { _, key -> key + 1 },
        )

        assertEquals(listOf("a", "b"), result.items)
        assertEquals(2, result.currentKey)
        assertFalse(result.isLoading)
        assertFalse(result.isEndReached)
        assertNull(result.error)
    }

    @Test
    fun `marks end reached when empty page returned`() = runTest {
        val result = Paginator.loadNext(
            currentState = initial.copy(items = listOf("a")),
            onRequest = { Outcome.Success(emptyList()) },
            getNextKey = { _, key -> key + 1 },
        )

        assertEquals(listOf("a"), result.items)
        assertTrue(result.isEndReached)
    }

    @Test
    fun `stores error message and keeps items on failure`() = runTest {
        val errorMessage = "Check your internet connection"
        val result = Paginator.loadNext(
            currentState = initial.copy(items = listOf("a"), currentKey = 2),
            onRequest = { Outcome.Failure(io.github.ayratka.demonews.domain.model.AppError.Network) },
            getNextKey = { _, key -> key + 1 },
        )

        assertEquals(listOf("a"), result.items)
        assertEquals(2, result.currentKey)
        assertFalse(result.isLoading)
        assertEquals("Check your internet connection", result.error)
    }

    @Test
    fun `rethrows CancellationException`() = runTest {
        val cancel = CancellationException("cancelled")
        try {
            Paginator.loadNext(
                currentState = initial,
                onRequest = { throw cancel },
                getNextKey = { _, key -> key + 1 },
            )
            throw AssertionError("Expected CancellationException")
        } catch (expected: CancellationException) {
            assertEquals(cancel, expected)
        }
    }

    @Test
    fun `returns same state when already loading`() = runTest {
        val loading = initial.copy(isLoading = true)
        var called = false

        val result = Paginator.loadNext(
            currentState = loading,
            onRequest = { called = true; Outcome.Success(emptyList()) },
            getNextKey = { _, key -> key + 1 },
        )

        assertEquals(loading, result)
        assertFalse("onRequest should not be called", called)
    }

    @Test
    fun `returns same state when end reached`() = runTest {
        val ended = initial.copy(isEndReached = true)
        var called = false

        val result = Paginator.loadNext(
            currentState = ended,
            onRequest = { called = true; Outcome.Success(emptyList()) },
            getNextKey = { _, key -> key + 1 },
        )

        assertEquals(ended, result)
        assertFalse("onRequest should not be called", called)
    }
}