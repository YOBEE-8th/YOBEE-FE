package com.ssafy.data.remote.datasource.account.dto

internal data class LoginRequestDto(
    val email: String,
    val password: String,
    val fcmToken: String,
)