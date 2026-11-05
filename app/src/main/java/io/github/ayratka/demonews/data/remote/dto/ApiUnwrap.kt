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