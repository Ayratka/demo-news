package io.github.ayratka.demonews.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val code: Int,
    val list: List<T>? = null,
    val news: T? = null,
){
    fun items(): List<T> = list.orEmpty()
    fun single(): T? = news
}