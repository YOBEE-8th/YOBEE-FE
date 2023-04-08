package com.ssafy.data.remote.service

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.auth.dto.AuthRequestDto
import com.ssafy.data.remote.datasource.auth.dto.AuthResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

internal interface AuthApiService {
    @POST("user/refresh")
    suspend fun validateToken(@Body authRequestDto: AuthRequestDto): BaseResponse<AuthResponseDto>

}