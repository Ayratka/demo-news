package io.github.ayratka.demonews.data.repository

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.github.ayratka.demonews.data.remote.api.NewsApi
import io.github.ayratka.demonews.data.remote.dto.ApiResponse
import io.github.ayratka.demonews.data.remote.dto.NewsDetailsDto
import io.github.ayratka.demonews.data.remote.mappers.NewsMapper
import io.github.ayratka.demonews.domain.model.AppError
import io.github.ayratka.demonews.domain.model.Outcome
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class NewsRepositoryImplTest {

    private lateinit var api: NewsApi
    private lateinit var repository: NewsRepositoryImpl

    @Before
    fun setup() {
        api = mock()
        repository = NewsRepositoryImpl(api, NewsMapper())
    }

    @Test
    fun `getNewsDetails maps code 0 to Success`() = runTest {
        val dto = NewsDetailsDto(42L, "Title", "2024-01-01T00:00:00Z", "Short", "Full")
        everySuspend { api.getNewsDetails(42L) } returns
                ApiResponse(code = 0, news = dto)

        val result = repository.getNewsDetails(42L)

        assertTrue("Expected Success, got $result", result is Outcome.Success)
        assertEquals(42L, (result as Outcome.Success).value.id)
    }

    @Test
    fun `getNewsDetails maps code != 0 to AppError_Api`() = runTest {
        everySuspend { api.getNewsDetails(any()) } returns
                ApiResponse(code = 404, news = null)

        val result = repository.getNewsDetails(42L)

        assertTrue(result is Outcome.Failure)
        assertEquals(AppError.Api(404, "Unknown server error"), (result as Outcome.Failure).error)
    }

    @Test
    fun `getNewsDetails maps HttpException to AppError_Http`() = runTest {
        val response = Response.error<NewsDetailsDto>(
            500, "".toResponseBody("application/json".toMediaType()),
        )
        everySuspend { api.getNewsDetails(any()) } throws HttpException(response)

        val result = repository.getNewsDetails(42L)

        assertTrue(result is Outcome.Failure)
        assertTrue((result as Outcome.Failure).error is AppError.Http)
    }

    @Test
    fun `getNewsDetails maps IOException to AppError_Network`() = runTest {
        everySuspend { api.getNewsDetails(any()) } throws IOException("timeout")

        val result = repository.getNewsDetails(42L)

        assertEquals(Outcome.Failure(AppError.Network), result)
    }

    @Test
    fun `getNewsDetails rethrows CancellationException`() = runTest {
        val cancel = CancellationException("cancelled")
        everySuspend { api.getNewsDetails(any()) } throws cancel

        try {
            repository.getNewsDetails(42L)
            fail("Expected CancellationException")
        } catch (e: CancellationException) {
            assertEquals(cancel, e)
        }
    }

    @Test
    fun `getNewsDetails returns Failure when code 0 but news is null`() = runTest {
        everySuspend { api.getNewsDetails(any()) } returns
                ApiResponse(code = 0, news = null)

        val result = repository.getNewsDetails(42L)

        assertTrue("Expected Failure for null payload, got $result", result is Outcome.Failure)
        assertTrue((result as Outcome.Failure).error is AppError.Api)
    }
}