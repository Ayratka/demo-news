package io.github.ayratka.demonews.domain

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import dev.mokkery.verify.VerifyMode.Companion.exactly
import io.github.ayratka.demonews.domain.model.AppError
import io.github.ayratka.demonews.domain.model.News
import io.github.ayratka.demonews.domain.model.Outcome
import io.github.ayratka.demonews.domain.repository.NewsRepository
import io.github.ayratka.demonews.domain.usecases.GetNewsByCategory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetNewsByCategoryTest {

    private lateinit var repository: NewsRepository
    private lateinit var useCase: GetNewsByCategory

    @Before
    fun setup() {
        repository = mock()
        useCase = GetNewsByCategory(repository)
    }

    @Test
    fun `invoke returns news for given category and page`() = runTest {
        val news = listOf(News(1L, "Title", "2024-01-01T00:00:00Z", "Short"))
        everySuspend { repository.getNewsByCategory(5L, 1) } returns Outcome.Success(news)

        val result = useCase(5L, 1)

        assertEquals(Outcome.Success(news), result)
        verifySuspend(exactly(1)) { repository.getNewsByCategory(5L, 1) }
    }

    @Test
    fun `invoke passes categoryId and page to repository`() = runTest {
        everySuspend { repository.getNewsByCategory(any(), any()) } returns
                Outcome.Success(emptyList())

        useCase(99L, 3)

        verifySuspend { repository.getNewsByCategory(99L, 3) }
    }

    @Test
    fun `invoke passes through failure outcome`() = runTest {
        everySuspend { repository.getNewsByCategory(any(), any()) } returns
                Outcome.Failure(AppError.Network)

        assertEquals(Outcome.Failure(AppError.Network), useCase(5L, 1))
    }
}