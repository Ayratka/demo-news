package io.github.ayratka.demonews.domain.model

import java.time.LocalDateTime

class NewsDetails(
    val id: Long,
    val title: String,
    val date: LocalDateTime,
    val shortDescription: String,
    val fullDescription: String,
) {
}