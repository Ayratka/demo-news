package io.github.ayratka.demonews.domain

import io.github.ayratka.demonews.domain.model.Category
import io.github.ayratka.demonews.domain.repository.CategoryRepository
import io.github.ayratka.demonews.domain.usecases.GetCategories
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetCategoriesTest {

    private lateinit var repository: CategoryRepository
    private lateinit var useCase: GetCategories

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetCategories(repository)
    }

    @Test
    fun `invoke should return categories from repository`() = runTest {
        // Given
        val expectedCategories = listOf(
            Category(id = 1, name = "Политика"),
            Category(id = 2, name = "Спорт")
        )
        coEvery { repository.getCategories() } returns expectedCategories

        // When
        val result = useCase()

        // Then
        assertEquals(expectedCategories, result)
        coVerify(exactly = 1) { repository.getCategories() }
    }

    @Test
    fun `invoke should return empty list when repository returns empty`() = runTest {
        // Given
        coEvery { repository.getCategories() } returns emptyList()

        // When
        val result = useCase()

        // Then
        assertEquals(emptyList<Category>(), result)
    }
}