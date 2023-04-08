package com.ssafy.data.remote.datasource.auth.dto

import com.google.gson.annotations.SerializedName

internal data class AuthRequestDto(
    @SerializedName("accessToken")
    val accessToken: String?,
    @SerializedName("refreshToken")
    val refreshToken: String?,
)