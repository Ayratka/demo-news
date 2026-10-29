package io.github.ayratka.demonews.domain

import io.github.ayratka.demonews.domain.model.NewsDetails
import io.github.ayratka.demonews.domain.repository.NewsRepository
import io.github.ayratka.demonews.domain.usecases.GetNewsDetails
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class GetNewsDetailsTest {

    private lateinit var repository: NewsRepository
    private lateinit var useCase: GetNewsDetails

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetNewsDetails(repository)
    }

    @Test
    fun `invoke should return news details for given id`() = runTest {
        // Given
        val newsId = 42
        val expectedDetails = NewsDetails(
            id = newsId,
            title = "Важная новость",
            shortDescription = "Краткое описание",
            fullDescription = "<p>Полный HTML контент</p>",
            date = LocalDateTime.now(),
        )
        coEvery { repository.getNewsDetails(newsId) } returns expectedDetails

        // When
        val result = useCase(newsId)

        // Then
        assertEquals(expectedDetails, result)
        coVerify(exactly = 1) { repository.getNewsDetails(newsId) }
    }

    @Test
    fun `invoke should pass correct newsId to repository`() = runTest {
        // Given
        val newsId = 99
        coEvery { repository.getNewsDetails(any()) } returns mockk()

        // When
        useCase(newsId)

        // Then
        coVerify { repository.getNewsDetails(newsId) }
    }

}