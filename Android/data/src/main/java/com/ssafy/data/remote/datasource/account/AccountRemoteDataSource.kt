package com.ssafy.data.remote.datasource.account

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.account.dto.*
import com.ssafy.data.remote.datasource.auth.dto.KakaoAuthResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody

internal interface AccountRemoteDataSource {
    suspend fun login(loginRequestDto: LoginRequestDto): BaseResponse<UserResponseDto>

    suspend fun validateEmail(validateEmailRequestDto: ValidateEmailRequestDto): BaseResponse<Void>

    suspend fun checkVerificationCode(checkVerificationCodeRequestDto: CheckVerificationCodeRequestDto): BaseResponse<Void>

    suspend fun validateNickname(validateNicknameRequestDto: ValidateNicknameRequestDto): BaseResponse<Void>
    suspend fun getKakaoAccountInfo(): KakaoAuthResponseDto
    suspend fun joinAccount(
        profileImage: MultipartBody.Part?,
        join: HashMap<String, RequestBody>,
    ): BaseResponse<AccountResponseDto>

    suspend fun autoLogin(autoLoginRequestDto: AutoLoginRequestDto): BaseResponse<AccountResponseDto>

    suspend fun socialLogin(socialLoginRequestDto: SocialLoginRequestDto): BaseResponse<AccountResponseDto>

    suspend fun logout(): BaseResponse<AccountResponseDto>

    suspend fun withdrawal(): BaseResponse<AccountResponseDto>

    suspend fun validateNicknameAuth(validateNicknameRequestDto: ValidateNicknameRequestDto): BaseResponse<Void>

    suspend fun updateProfile(
        profileImage: MultipartBody.Part?,
        nickname: RequestBody,
    ): BaseResponse<AccountResponseDto>

    suspend fun changePassword(changePasswordRequestDto: ChangePasswordRequestDto): BaseResponse<AccountResponseDto>

    suspend fun resetPassword(resetPasswordRequestDto: ResetPasswordRequestDto): BaseResponse<Void>
}