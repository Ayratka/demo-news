package io.github.ayratka.demonews.data.remote.dto

import io.github.ayratka.demonews.domain.model.AppError
import io.github.ayratka.demonews.domain.model.Outcome

private const val SUCCESS_CODE = 0

fun <T> requireSuccessList(
    code: Int,
    list: List<T>?,
    message: String? = null,
): Outcome<List<T>> =
    if (code == SUCCESS_CODE) Outcome.Success(list.orEmpty())
    else Outcome.Failure(AppError.Api(code, message ?: "Unknown server error"))

fun <T : Any> requireSuccessSingle(
    code: Int,
    data: T? = null,
    message: String? = null,
): Outcome<T> =
    when {
        code != SUCCESS_CODE ->
            Outcome.Failure(AppError.Api(code, message ?: "Unknown server error"))

        data == null ->
            Outcome.Failure(AppError.Api(code, "Server returned code=0 but no payload"))

        else ->
            Outcome.Success(data)
    }