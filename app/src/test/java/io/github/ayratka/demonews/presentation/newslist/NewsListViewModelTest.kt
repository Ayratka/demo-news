package io.github.ayratka.demonews.presentation.newslist

import androidx.lifecycle.SavedStateHandle
import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentially
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.github.ayratka.demonews.MainDispatcherRule
import io.github.ayratka.demonews.domain.model.AppError
import io.github.ayratka.demonews.domain.model.News
import io.github.ayratka.demonews.domain.model.Outcome
import io.github.ayratka.demonews.domain.repository.NewsRepository
import io.github.ayratka.demonews.domain.usecases.GetNewsByCategory
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.bytebuddy.matcher.ElementMatchers.returns
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NewsListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: NewsRepository = mock()
    private val getNewsByCategory = GetNewsByCategory(repository)

    private fun savedState(categoryId: Long = 5L) =
        SavedStateHandle(mapOf("categoryId" to categoryId))

    @Test
    fun `init loads first page`() = runTest(mainDispatcherRule.dispatcher) {
        everySuspend { repository.getNewsByCategory(any(), any()) } returns
                Outcome.Success(listOf(news(1), news(2)))

        val vm = NewsListViewModel(savedState(), getNewsByCategory)
        val job = launch { vm.state.collect { } }
        advanceUntilIdle()

        assertEquals(listOf(news(1), news(2)), vm.state.value.items)
        assertEquals(1, vm.state.value.currentKey)
        assertNull(vm.state.value.error)

        job.cancel()
    }

    @Test
    fun `loadMore appends next page`() = runTest(mainDispatcherRule.dispatcher) {
        everySuspend { repository.getNewsByCategory(5L, 0) } returns
                Outcome.Success(listOf(news(1)))
        everySuspend { repository.getNewsByCategory(5L, 1) } returns
                Outcome.Success(listOf(news(2)))

        val vm = NewsListViewModel(savedState(), getNewsByCategory)
        val job = launch { vm.state.collect { } }
        advanceUntilIdle()
        assertEquals(listOf(news(1)), vm.state.value.items)
        assertEquals(1, vm.state.value.currentKey)

        vm.loadMore()
        advanceUntilIdle()

        assertEquals(listOf(news(1), news(2)), vm.state.value.items)
        assertEquals(2, vm.state.value.currentKey)

        job.cancel()
    }


    @Test
    fun `empty page marks end reached`() = runTest(mainDispatcherRule.dispatcher) {
        everySuspend { repository.getNewsByCategory(any(), any()) } returns
                Outcome.Success(emptyList())

        val vm = NewsListViewModel(savedState(), getNewsByCategory)
        advanceUntilIdle()

        assertTrue(vm.state.value.items.isEmpty())
        assertTrue(vm.state.value.isEndReached)
        assertTrue(vm.state.value.isEmpty)
    }

    @Test
    fun `initial error is captured`() = runTest(mainDispatcherRule.dispatcher) {
        everySuspend { repository.getNewsByCategory(any(), any()) } returns
                Outcome.Failure(AppError.Network)

        val vm = NewsListViewModel(savedState(), getNewsByCategory)
        advanceUntilIdle()

        assertEquals("Check your internet connection", vm.state.value.error)
        assertTrue(vm.state.value.items.isEmpty())
        assertNotNull(vm.state.value.initialError)
        assertNull(vm.state.value.appendError)
    }

    @Test
    fun `retry reloadsafter initial error`() = runTest(mainDispatcherRule.dispatcher) {
        // Один стаб, два ответа по очереди: первый вызов — Failure, второй — Success
        everySuspend { repository.getNewsByCategory(any(), any()) } sequentially {
            returns(Outcome.Failure(AppError.Network))
            returns(Outcome.Success(listOf(news(1))))
        }

        val vm = NewsListViewModel(savedState(), getNewsByCategory)
        advanceUntilIdle()
        assertEquals("Check your internet connection", vm.state.value.error)

        vm.retry()
        advanceUntilIdle()

        assertEquals(listOf(news(1)), vm.state.value.items)
        assertNull(vm.state.value.error)
    }

    @Test
    fun `append error keeps list and exposes footer error`() =
        runTest(mainDispatcherRule.dispatcher) {
            everySuspend { repository.getNewsByCategory(5L, 0) } returns
                    Outcome.Success(listOf(news(1)))
            everySuspend { repository.getNewsByCategory(5L, 1) } returns
                    Outcome.Failure(AppError.Network)

            val vm = NewsListViewModel(savedState(), getNewsByCategory)
            advanceUntilIdle()
            assertEquals(listOf(news(1)), vm.state.value.items)

            vm.loadMore()
            advanceUntilIdle()

            assertEquals(listOf(news(1)), vm.state.value.items)
            assertEquals("Check your internet connection", vm.state.value.error)
            assertNull(vm.state.value.initialError)
            assertNotNull(vm.state.value.appendError)
            assertEquals(1, vm.state.value.currentKey)   // ← 1, не 2
        }
    private fun news(id: Long) = News(
        id = id,
        title = "News $id",
        date = "2024-01-01T00:00:00Z",
        shortDescription = "Description $id",
    )
}