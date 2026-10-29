package io.github.ayratka.demonews.domain

import io.github.ayratka.demonews.domain.model.News
import io.github.ayratka.demonews.domain.repository.NewsRepository
import io.github.ayratka.demonews.domain.usecases.GetNewsByCategory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetNewsByCategoryTest {

    private lateinit var repository: NewsRepository
    private lateinit var useCase: GetNewsByCategory

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetNewsByCategory(repository)
    }

    @Test
    fun `invoke should return news for given category`() = runTest {
        // Given
        val categoryId = 1
        val page = 1
        val expectedNews = listOf(
            News(
                id = 1,
                title = "Новость 1",
                shortDescription = "Описание 1",
                date = LocalDateTime.now(),
            )
        )
        coEvery {
            repository.getNewsByCategory(categoryId, page)
        } returns expectedNews

        // When
        val result = useCase(categoryId, page)

        // Then
        assertEquals(expectedNews, result)
        coVerify(exactly = 1) {
            repository.getNewsByCategory(categoryId, page)
        }
    }

    @Test
    fun `invoke should pass correct parameters to repository`() = runTest {
        // Given
        val categoryId = 5
        val page = 3
        coEvery {
            repository.getNewsByCategory(any(), any())
        } returns emptyList()

        // When
        useCase(categoryId, page)

        // Then
        coVerify {
            repository.getNewsByCategory(categoryId, page)
        }
    }
}