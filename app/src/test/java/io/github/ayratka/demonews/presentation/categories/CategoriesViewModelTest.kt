package io.github.ayratka.demonews.presentation.categories

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import io.github.ayratka.demonews.MainDispatcherRule
import io.github.ayratka.demonews.domain.model.AppError
import io.github.ayratka.demonews.domain.model.Category
import io.github.ayratka.demonews.domain.model.Outcome
import io.github.ayratka.demonews.domain.repository.CategoryRepository
import io.github.ayratka.demonews.domain.usecases.GetCategories
import io.github.ayratka.demonews.presentation.categories.CategoriesUiState
import io.github.ayratka.demonews.presentation.categories.CategoriesViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CategoriesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository: CategoryRepository = mock()
    private val getCategories = GetCategories(repository)

    @Test
    fun `emits Success with categories`() = runTest(mainDispatcherRule.dispatcher) {
        val categories = listOf(Category(1L, "Tech"), Category(2L, "Sports"))
        everySuspend { repository.getCategories() } returns Outcome.Success(categories)

        val vm = CategoriesViewModel(getCategories)

        // Ручная подписка — запускает upstream (WhileSubscribed)
        val job = launch { vm.uiState.collect { } }
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue("Expected Success, got $state", state is CategoriesUiState.Success)
        assertEquals(categories, (state as CategoriesUiState.Success).categories)

        job.cancel()
    }

    @Test
    fun `emits Empty on empty list`() = runTest(mainDispatcherRule.dispatcher) {
        everySuspend { repository.getCategories() } returns Outcome.Success(emptyList())

        val vm = CategoriesViewModel(getCategories)

        val job = launch { vm.uiState.collect { } }
        advanceUntilIdle()

        assertEquals(CategoriesUiState.Empty, vm.uiState.value)

        job.cancel()
    }

    @Test
    fun `emits Error on failure`() = runTest(mainDispatcherRule.dispatcher) {
        everySuspend { repository.getCategories() } returns Outcome.Failure(AppError.Network)

        val vm = CategoriesViewModel(getCategories)

        val job = launch { vm.uiState.collect { } }
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue("Expected Error, got $state", state is CategoriesUiState.Error)
        assertEquals("Check your internet connection", (state as CategoriesUiState.Error).message)

        job.cancel()
    }

    @Test
    fun `retry reloads after failure`() = runTest(mainDispatcherRule.dispatcher) {
        everySuspend { repository.getCategories() } returns Outcome.Failure(AppError.Network)

        val vm = CategoriesViewModel(getCategories)

        val job = launch { vm.uiState.collect { } }
        advanceUntilIdle()

        assertTrue(vm.uiState.value is CategoriesUiState.Error)

        // переопределяем стаб: следующий вызов вернёт Success
        val categories = listOf(Category(1L, "Tech"))
        everySuspend { repository.getCategories() } returns Outcome.Success(categories)

        vm.retry()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue("Expected Success, got $state", state is CategoriesUiState.Success)
        assertEquals(categories, (state as CategoriesUiState.Success).categories)

        job.cancel()
    }
}