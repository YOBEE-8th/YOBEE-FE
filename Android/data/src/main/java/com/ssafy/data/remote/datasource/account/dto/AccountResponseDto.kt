package com.ssafy.data.remote.datasource.account.dto

import com.google.gson.annotations.SerializedName

internal data class AccountResponseDto(
    val userId: Int,
    val email: String,
    val password: String,
    val type: Int,
    val fcmToken: String,
    val accessToken: String,
    val refreshToken: String,
    @SerializedName("nickname")
    val nickName: String,
    val profileImage: String?,
    val level: Int,
)