package io.github.ayratka.demonews.data.remote.dto

data class NewsDto(
    val id: Int,
    val title: String,
    val date: String,
    val shortDescription: String,
)