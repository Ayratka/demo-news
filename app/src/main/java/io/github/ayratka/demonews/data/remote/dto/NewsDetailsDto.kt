package io.github.ayratka.demonews.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewsDetailsDto(
    val id: Long,
    val title: String,
    val date: String,
    val shortDescription: String,
    val fullDescription: String,
)