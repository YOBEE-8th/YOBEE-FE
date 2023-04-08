package com.ssafy.data.remote.datasource.auth

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.auth.dto.AuthRequestDto
import com.ssafy.data.remote.datasource.auth.dto.AuthResponseDto
import com.ssafy.data.remote.service.AuthApiService
import javax.inject.Inject

private const val TAG = "UserRemoteDataSourceImp_요비"

internal class AuthRemoteDataSourceImpl @Inject constructor(private val authApiService: AuthApiService) :
    AuthRemoteDataSource {
    override suspend fun validateToken(authRequestDto: AuthRequestDto): BaseResponse<AuthResponseDto> {
        return authApiService.validateToken(authRequestDto)
    }


}
