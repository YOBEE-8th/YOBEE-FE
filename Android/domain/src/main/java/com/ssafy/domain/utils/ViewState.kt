package com.ssafy.domain.utils

sealed class ViewState<T>(
    val value: T? = null,
    val message: String? = null,
) {
    class Success<T>(message: String?, data: T?) : ViewState<T>(data, message)
    class Loading<Nothing> : ViewState<Nothing>()
    class Error<T>(message: String?, data: T? = null) : ViewState<T>(data, message)
}
