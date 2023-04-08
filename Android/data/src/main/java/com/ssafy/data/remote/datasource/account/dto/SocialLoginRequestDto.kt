package com.ssafy.data.remote.datasource.account.dto

internal data class SocialLoginRequestDto(
    val email: String,
    val type: Int,
    val fcmToken: String,
)