package io.github.ayratka.demonews.data.remote.dto

private const val SUCCESS_CODE = 0

fun <T> requireSuccess(
    code: Int,
    payload: T?,
    message: String? = null,
): T {
    if (code != SUCCESS_CODE) {
        throw ApiException(code, message ?: "Unknown server error")
    }
    return checkNotNull(payload) { "code=0 but payload is null" }
}

fun <T> requireSuccessList(
    code: Int,
    list: List<T>?,
    message: String? = null,
): List<T> {
    if (code != SUCCESS_CODE) {
        throw ApiException(code, message ?: "Unknown server error")
    }
    return list.orEmpty()
}