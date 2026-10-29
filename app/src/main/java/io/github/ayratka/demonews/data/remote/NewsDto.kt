package io.github.ayratka.demonews.data.remote

import java.time.LocalDateTime

data class NewsDto(
    val id: Int,
    val title: String,
    val date: String,
    val shortDescription: String,
)