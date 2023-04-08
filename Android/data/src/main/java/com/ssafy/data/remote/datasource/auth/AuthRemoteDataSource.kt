package com.ssafy.data.remote.datasource.auth

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.auth.dto.AuthRequestDto
import com.ssafy.data.remote.datasource.auth.dto.AuthResponseDto

internal interface AuthRemoteDataSource {
    suspend fun validateToken(authRequestDto: AuthRequestDto): BaseResponse<AuthResponseDto>


}