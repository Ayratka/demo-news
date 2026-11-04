package io.github.ayratka.demonews.data.remote.dto

import com.squareup.moshi.JsonClass

class ApiException(
    val code: Int,
    override val message: String,
) : Exception("API error $code: $message")