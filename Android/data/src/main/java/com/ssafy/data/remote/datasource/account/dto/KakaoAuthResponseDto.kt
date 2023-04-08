package com.ssafy.data.remote.datasource.auth.dto

import com.google.gson.annotations.SerializedName

data class KakaoAuthResponseDto(
    val id: Long,
    val properties: Properties,
    @SerializedName("kakao_account")
    val kakaoAccount: KakaoAccount,
)


data class Properties(
    val nickname: String,
    val profile_image: String,
    val thumbnail_image: String,
)

data class KakaoAccount(
    val email: String?,
)

