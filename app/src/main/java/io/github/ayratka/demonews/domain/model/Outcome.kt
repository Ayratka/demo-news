package io.github.ayratka.demonews.domain.model

sealed interface Outcome<out T> {

    data class Success<T>(val value: T) : Outcome<T>

    data class Failure(val error: AppError) : Outcome<Nothing>


    fun <R> map(transform: (T) -> R): Outcome<R> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }

    fun <R> flatMap(transform: (T) -> Outcome<R>): Outcome<R> = when (this) {
        is Success -> transform(value)
        is Failure -> this
    }

    fun mapError(transform: (AppError) -> AppError): Outcome<T> = when (this) {
        is Success -> this
        is Failure -> Failure(transform(error))
    }

    fun getOrNull(): T? = (this as? Success)?.value

    fun errorOrNull(): AppError? = (this as? Failure)?.error

}