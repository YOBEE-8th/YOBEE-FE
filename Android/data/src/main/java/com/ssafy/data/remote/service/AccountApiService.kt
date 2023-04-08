package com.ssafy.data.remote.service

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.account.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

internal interface AccountApiService {
    @POST("user/login")
    suspend fun login(@Body loginRequestDto: LoginRequestDto): BaseResponse<UserResponseDto>

    @POST("user/email/duplicate")
    suspend fun validateEmail(@Body validateEmailRequestDto: ValidateEmailRequestDto): BaseResponse<Void>

    @POST("user/email/auth/check")
    suspend fun checkVerificationCode(@Body checkVerificationCodeRequestDto: CheckVerificationCodeRequestDto): BaseResponse<Void>

    @POST("user/nickname/duplicate")
    suspend fun validateNickname(@Body validateNicknameRequestDto: ValidateNicknameRequestDto): BaseResponse<Void>

    @Multipart
    @POST("user/signup")
    suspend fun joinAccount(
        @Part profileImage: MultipartBody.Part?,
        @PartMap createUserDto: HashMap<String, RequestBody>,
    ): BaseResponse<AccountResponseDto>

    @POST("user/login")
    suspend fun autoLogin(@Body loginRequestDto: AutoLoginRequestDto): BaseResponse<AccountResponseDto>

    @POST("user/login/social")
    suspend fun socialLogin(@Body socialLoginRequestDto: SocialLoginRequestDto): BaseResponse<AccountResponseDto>

    @POST("user/email/issue/password")
    suspend fun resetPassword(@Body resetPasswordRequestDto: ResetPasswordRequestDto): BaseResponse<Void>
}
