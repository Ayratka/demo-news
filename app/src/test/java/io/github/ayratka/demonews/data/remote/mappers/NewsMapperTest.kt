package io.github.ayratka.demonews.data.remote.mappers

import io.github.ayratka.demonews.data.remote.dto.NewsDetailsDto
import io.github.ayratka.demonews.data.remote.dto.NewsDto
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test

class NewsMapperTest {

    private lateinit var mapper: NewsMapper

    @Before
    fun setup() {
        mapper = NewsMapper()
    }

    @Test
    fun `mapToDomain should map NewsDto correctly`() {
        // Given
        val dto = NewsDto(
            id = 1,
            title = "Title",
            date = "2016-08-03T11:15:21.104Z",
            shortDescription = "Short",
        )

        // When
        val result = mapper.mapToDomain(dto)

        // Then
        assertEquals(1, result.id)
        assertEquals("Title", result.title)
        assertEquals("Short", result.shortDescription)
        assertEquals(2016, result.date.year)
    }

    @Test
    fun `mapDetailsToDomain should map NewsDetailsDto correctly`() {
        // Given
        val dto = NewsDetailsDto(
            id = 1,
            title = "Title",
            shortDescription = "Short",
            fullDescription = "<p>Full HTML</p>",
            date = "2016-08-03T11:15:21.104Z",
        )

        // When
        val result = mapper.mapDetailsToDomain(dto)

        // Then
        assertEquals("<p>Full HTML</p>", result.fullDescription)
    }

    @Test
    fun `parseDate should handle invalid date gracefully`() {
        // Given
        val dto = NewsDto(
            id = 1,
            title = "Title",
            shortDescription = "Short",
            date = "invalid-date",
        )

        // When
        val result = mapper.mapToDomain(dto)

        // Then
        assertNotNull(result.date)
    }
}