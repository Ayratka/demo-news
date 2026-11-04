package io.github.ayratka.demonews.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CategoryDto(
    val id: Long,
    val name: String,
)