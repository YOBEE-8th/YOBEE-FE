package com.ssafy.data.remote.datasource.account.dto

internal data class AutoLoginRequestDto(
    val email: String,
    val password: String,
    val fcmToken: String,
)