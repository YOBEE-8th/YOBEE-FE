package com.ssafy.data.remote.datasource.account

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.account.dto.*
import com.ssafy.data.remote.datasource.auth.dto.KakaoAuthResponseDto
import com.ssafy.data.remote.service.AccountApiService
import com.ssafy.data.remote.service.AccountAuthApiService
import com.ssafy.data.remote.service.KakaoApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

private const val TAG = "AccountRemoteDataSourceImpl"

internal class AccountRemoteDataSourceImpl @Inject constructor(
    private val accountApiService: AccountApiService,
    private val accountAuthApiService: AccountAuthApiService,
    private val kakaoApiService: KakaoApiService,
) :
    AccountRemoteDataSource {
    override suspend fun login(loginRequestDto: LoginRequestDto): BaseResponse<UserResponseDto> {
        return accountApiService.login(loginRequestDto)
    }

    override suspend fun validateEmail(validateEmailRequestDto: ValidateEmailRequestDto): BaseResponse<Void> {
        return accountApiService.validateEmail(validateEmailRequestDto)
    }

    override suspend fun checkVerificationCode(checkVerificationCodeRequestDto: CheckVerificationCodeRequestDto): BaseResponse<Void> {
        return accountApiService.checkVerificationCode(checkVerificationCodeRequestDto)
    }

    override suspend fun validateNickname(validateNicknameRequestDto: ValidateNicknameRequestDto): BaseResponse<Void> {
        return accountApiService.validateNickname(validateNicknameRequestDto)
    }

    override suspend fun joinAccount(
        profileImage: MultipartBody.Part?,
        join: HashMap<String, RequestBody>,
    ): BaseResponse<AccountResponseDto> {
        return accountApiService.joinAccount(profileImage, join)
    }

    override suspend fun autoLogin(autoLoginRequestDto: AutoLoginRequestDto): BaseResponse<AccountResponseDto> {
        return accountApiService.autoLogin(autoLoginRequestDto)
    }

    override suspend fun socialLogin(socialLoginRequestDto: SocialLoginRequestDto): BaseResponse<AccountResponseDto> {
        return accountApiService.socialLogin(socialLoginRequestDto)
    }

    override suspend fun logout(): BaseResponse<AccountResponseDto> {
        return accountAuthApiService.logout()
    }

    override suspend fun withdrawal(): BaseResponse<AccountResponseDto> {
        return accountAuthApiService.withdrawal()
    }

    override suspend fun validateNicknameAuth(validateNicknameRequestDto: ValidateNicknameRequestDto): BaseResponse<Void> {
        return accountAuthApiService.validateNicknameAuth(validateNicknameRequestDto)
    }

    override suspend fun updateProfile(
        profileImage: MultipartBody.Part?,
        nickname: RequestBody,
    ): BaseResponse<AccountResponseDto> {
        return accountAuthApiService.updateUserProfile(profileImage, nickname)
    }

    override suspend fun changePassword(changePasswordRequestDto: ChangePasswordRequestDto): BaseResponse<AccountResponseDto> {
        return accountAuthApiService.changePassword(changePasswordRequestDto)
    }

    override suspend fun resetPassword(resetPasswordRequestDto: ResetPasswordRequestDto): BaseResponse<Void> {
        return accountApiService.resetPassword(resetPasswordRequestDto)
    }

    override suspend fun getKakaoAccountInfo(): KakaoAuthResponseDto =
        kakaoApiService.getKakaoAccountInfo()
}
