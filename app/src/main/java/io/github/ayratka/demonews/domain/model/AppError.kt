package io.github.ayratka.demonews.domain.model

sealed interface AppError {
    val message: String

    data object Network : AppError {
        override val message = "Check your internet connection"
    }

    data class Api(val code: Int, override val message: String) : AppError

    data class Http(val httpCode: Int, override val message: String) : AppError

    data object Unknown : AppError {
        override val message = "Something went wrong. Please try again."
    }
}