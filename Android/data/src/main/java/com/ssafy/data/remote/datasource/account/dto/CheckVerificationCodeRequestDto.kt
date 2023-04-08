package com.ssafy.data.remote.datasource.account.dto

internal data class CheckVerificationCodeRequestDto(
    val emailToken: String,
)