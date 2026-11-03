package io.github.ayratka.demonews.domain.model

import java.time.LocalDateTime

data class News(
    val id: Long,
    val title: String,
    val date: LocalDateTime,
    val shortDescription: String,
)