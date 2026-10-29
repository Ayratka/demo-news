package io.github.ayratka.demonews.data.remote.mappers

import io.github.ayratka.demonews.data.remote.dto.NewsDetailsDto
import io.github.ayratka.demonews.data.remote.dto.NewsDto
import io.github.ayratka.demonews.domain.model.News
import io.github.ayratka.demonews.domain.model.NewsDetails
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class NewsMapper @Inject constructor() {

    fun mapToDomain(dto: NewsDto): News {
        return News(
            id = dto.id,
            title = dto.title,
            shortDescription = dto.shortDescription,
            date = parseDate(dto.date),
        )
    }

    fun mapDetailsToDomain(newsDetailsDto: NewsDetailsDto): NewsDetails = NewsDetails(
        id = newsDetailsDto.id,
        title = newsDetailsDto.title,
        date = parseDate(newsDetailsDto.date),
        shortDescription = newsDetailsDto.shortDescription,
        fullDescription = newsDetailsDto.fullDescription,
    )

    private fun parseDate(dateString: String): LocalDateTime {
        return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
    }
}