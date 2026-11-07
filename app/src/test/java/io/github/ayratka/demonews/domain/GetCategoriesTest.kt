package io.github.ayratka.demonews.domain

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import io.github.ayratka.demonews.domain.model.AppError
import io.github.ayratka.demonews.domain.model.Category
import io.github.ayratka.demonews.domain.model.Outcome
import io.github.ayratka.demonews.domain.repository.CategoryRepository
import io.github.ayratka.demonews.domain.usecases.GetCategories
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.collections.emptyList

class GetCategoriesTest {

    private lateinit var repository: CategoryRepository
    private lateinit var useCase: GetCategories

    @Before
    fun setup() {
        repository = mock()
        useCase = GetCategories(repository)
    }

    @Test
    fun `invoke returns categories from repository`() = runTest {
        val categories = listOf(Category(1L, "Tech"), Category(2L, "Sports"))
        everySuspend { repository.getCategories() } returns Outcome.Success(categories)

        val result = useCase()

        assertEquals(Outcome.Success(categories), result)
        verifySuspend(exactly(1)) { repository.getCategories() }
    }

    @Test
    fun `invoke passes through failure outcome`() = runTest {
        val error = AppError.Network
        everySuspend { repository.getCategories() } returns Outcome.Failure(error)

        assertEquals(Outcome.Failure(error), useCase())
    }

    @Test
    fun `invoke returns empty list on empty success`() = runTest {
        everySuspend { repository.getCategories() } returns Outcome.Success(emptyList())

        val result = useCase()

        assertEquals(Outcome.Success(emptyList<Category>()), result)
    }
}