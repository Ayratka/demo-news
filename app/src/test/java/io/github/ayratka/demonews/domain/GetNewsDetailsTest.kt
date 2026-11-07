package io.github.ayratka.demonews.domain
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import dev.mokkery.verify.VerifyMode.Companion.exactly
import io.github.ayratka.demonews.domain.model.AppError
import io.github.ayratka.demonews.domain.model.NewsDetails
import io.github.ayratka.demonews.domain.model.Outcome
import io.github.ayratka.demonews.domain.repository.NewsRepository
import io.github.ayratka.demonews.domain.usecases.GetNewsDetails
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetNewsDetailsTest {

    private lateinit var repository: NewsRepository
    private lateinit var useCase: GetNewsDetails

    @Before
    fun setup() {
        repository = mock()
        useCase = GetNewsDetails(repository)
    }

    @Test
    fun `invoke returns details from repository`() = runTest {
        val details = NewsDetails(42L, "Title", "2024-01-01T00:00:00Z", "Short", "Full")
        everySuspend { repository.getNewsDetails(42L) } returns Outcome.Success(details)

        val result = useCase(42L)

        assertEquals(Outcome.Success(details), result)
        verifySuspend(exactly(1)) { repository.getNewsDetails(42L) }
    }

    @Test
    fun `invoke passes newsId to repository`() = runTest {
        everySuspend { repository.getNewsDetails(any()) } returns
                Outcome.Success(NewsDetails(1L, "T", "2024-01-01T00:00:00Z", "S", "F"))

        useCase(99L)

        verifySuspend { repository.getNewsDetails(99L) }
    }

    @Test
    fun `invoke passes through failure outcome`() = runTest {
        everySuspend { repository.getNewsDetails(any()) } returns
                Outcome.Failure(AppError.Api(404, "Not found"))

        assertEquals(
            Outcome.Failure(AppError.Api(404, "Not found")),
            useCase(42L),
        )
    }
}