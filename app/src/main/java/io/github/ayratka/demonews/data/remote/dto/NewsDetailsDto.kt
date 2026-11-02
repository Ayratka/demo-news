package io.github.ayratka.demonews.data.remote.dto

data class NewsDetailsDto(
    val id: Int,
    val title: String,
    val date: String,
    val shortDescription: String,
    val fullDescription: String,
)