package com.ssafy.data.remote.service

import com.ssafy.data.remote.datasource.auth.dto.KakaoAuthResponseDto
import retrofit2.http.POST

internal interface KakaoApiService {
    @POST("me")
    suspend fun getKakaoAccountInfo(): KakaoAuthResponseDto
}