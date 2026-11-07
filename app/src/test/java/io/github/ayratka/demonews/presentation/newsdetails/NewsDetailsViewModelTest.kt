package io.github.ayratka.demonews.presentation.newsdetails


import androidx.lifecycle.SavedStateHandle
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.github.ayratka.demonews.MainDispatcherRule
import io.github.ayratka.demonews.domain.model.AppError
import io.github.ayratka.demonews.domain.model.NewsDetails
import io.github.ayratka.demonews.domain.model.Outcome
import io.github.ayratka.demonews.domain.repository.NewsRepository
import io.github.ayratka.demonews.domain.usecases.GetNewsDetails
import io.github.ayratka.demonews.presentation.newsdetails.NewsDetailsUiState
import io.github.ayratka.demonews.presentation.newsdetails.NewsDetailsViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NewsDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: NewsRepository = mock()
    private val getNewsDetails = GetNewsDetails(repository)

    private fun savedState(newsId: Long = 42L) =
        SavedStateHandle(mapOf("newsId" to newsId))

    @Test
    fun `emits Success with details`() = runTest(mainDispatcherRule.dispatcher) {
        val details = details(42L)
        everySuspend { repository.getNewsDetails(42L) } returns Outcome.Success(details)

        val vm = NewsDetailsViewModel(savedState(), getNewsDetails)
        val job = launch { vm.uiState.collect { } }   // держим подписку, если stateIn
        advanceUntilIdle()

        val state = vm.uiState.value
        println("STATE = $state")   // диагностика: если тут Loading — смотри ниже

        assertTrue("Expected Success, got $state", state is NewsDetailsUiState.Success)
        assertEquals(details, (state as NewsDetailsUiState.Success).details)

        job.cancel()
    }

    @Test
    fun `emits Error on failure`() = runTest(mainDispatcherRule.dispatcher) {
        everySuspend { repository.getNewsDetails(any()) } returns
                Outcome.Failure(AppError.Api(404, "Not found"))

        val vm = NewsDetailsViewModel(savedState(), getNewsDetails)
        val job = launch { vm.uiState.collect { } }
        advanceUntilIdle()

        val state = vm.uiState.value
        println("STATE = $state")

        assertTrue("Expected Error, got $state", state is NewsDetailsUiState.Error)
        assertEquals("Not found", (state as NewsDetailsUiState.Error).message)

        job.cancel()
    }

    @Test
    fun `retry reloads after failure`() = runTest(mainDispatcherRule.dispatcher) {
        everySuspend { repository.getNewsDetails(any()) } returns Outcome.Failure(AppError.Network)

        val vm = NewsDetailsViewModel(savedState(), getNewsDetails)
        val job = launch { vm.uiState.collect { } }
        advanceUntilIdle()
        assertTrue(vm.uiState.value is NewsDetailsUiState.Error)

        val details = details(42L)
        everySuspend { repository.getNewsDetails(any()) } returns Outcome.Success(details)

        vm.retry()
        advanceUntilIdle()

        val state = vm.uiState.value
        println("STATE after retry = $state")

        assertTrue("Expected Success, got $state", state is NewsDetailsUiState.Success)
        assertEquals(details, (state as NewsDetailsUiState.Success).details)

        job.cancel()
    }

    private fun details(id: Long) = NewsDetails(
        id = id,
        title = "Title",
        date = "2024-01-01T00:00:00Z",
        shortDescription = "Short",
        fullDescription = "Full",
    )
}