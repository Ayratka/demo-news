package io.github.ayratka.demonews.data.remote

import java.time.LocalDateTime

data class NewsDetailsDto(
    val id: Int,
    val title: String,
    val date: String,
    val shortDescription: String,
    val fullDescription: String,
)