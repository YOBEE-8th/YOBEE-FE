package com.ssafy.data.remote.service

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.account.dto.AccountResponseDto
import com.ssafy.data.remote.datasource.account.dto.ChangePasswordRequestDto
import com.ssafy.data.remote.datasource.account.dto.ValidateNicknameRequestDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

internal interface AccountAuthApiService {
    @POST("user/logout")
    suspend fun logout(): BaseResponse<AccountResponseDto>

    @DELETE("user/withdrawal")
    suspend fun withdrawal(): BaseResponse<AccountResponseDto>

    @POST("user/nickname/duplicate/auth")
    suspend fun validateNicknameAuth(@Body validateNicknameRequestDto: ValidateNicknameRequestDto): BaseResponse<Void>

    @Multipart
    @POST("user/info/change")
    suspend fun updateUserProfile(
        @Part profileImage: MultipartBody.Part?,
        @Part("nickname") nickname: RequestBody,
    ): BaseResponse<AccountResponseDto>

    @PUT("user/password/change")
    suspend fun changePassword(@Body changePasswordRequestDto: ChangePasswordRequestDto): BaseResponse<AccountResponseDto>
}