package io.github.ayratka.demonews.domain

import java.time.LocalDateTime

class NewsDetails(
    val id: Int,
    val title: String,
    val date: LocalDateTime,
    val shortDescription: String,
    val fullDescription: String,
) {
}