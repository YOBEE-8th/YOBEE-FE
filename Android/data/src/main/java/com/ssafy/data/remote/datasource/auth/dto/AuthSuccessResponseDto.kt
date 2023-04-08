package com.ssafy.data.remote.datasource.auth.dto

import com.google.gson.annotations.SerializedName

internal data class AuthSuccessResponseDto(
    @SerializedName("accessToken")
    var accessToken: String?,
    @SerializedName("accessTokenExpiresIn")
    var accessTokenExpiresIn: Long?,
    @SerializedName("grantType")
    var grantType: String?,
    @SerializedName("refreshToken")
    var refreshToken: String?,
)

internal data class AuthResponseDto(
    @SerializedName("accessToken")
    var accessToken: String?,
    @SerializedName("refreshToken")
    var refreshToken: String?,
)