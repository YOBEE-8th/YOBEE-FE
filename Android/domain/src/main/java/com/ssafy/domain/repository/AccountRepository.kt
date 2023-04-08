package com.ssafy.domain.repository

import com.ssafy.domain.model.account.*
import com.ssafy.domain.utils.ViewState
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AccountRepository {
    suspend fun login(
        email: String,
        password: String,
        fcmToken: String,
    ): ViewState<UserInfoDomainModel>

    suspend fun validateEmail(email: String): ViewState<Void>

    suspend fun checkVerificationCode(verificationCode: String): ViewState<Void>

    suspend fun validateNickname(nickname: String): ViewState<Void>

    suspend fun joinAccount(
        profileImage: MultipartBody.Part?,
        join: HashMap<String, RequestBody>,
    ): ViewState<AccountnfoDomainModel>

    suspend fun autoLogin(
        email: String,
        password: String,
        fcmToken: String,
    ): ViewState<AccountnfoDomainModel>

    suspend fun socialLogin(
        email: String,
        type: Int,
        fcmToken: String,
    ): ViewState<AccountnfoDomainModel>

    fun autoLoginCheck(): String?

    suspend fun getNickname(): ViewState<String>

    suspend fun getProfileImage(): ViewState<String>

    suspend fun logout(): ViewState<LogoutDomainModel>

    suspend fun withdrawal(): ViewState<WithdrawalDomainModel>

    fun getProfile(): ProfileDomainModel

    suspend fun validateNicknameAuth(nickname: String): ViewState<Void>

    suspend fun updateProfile(
        profileImage: MultipartBody.Part?,
        nickname: RequestBody,
    ): ViewState<ProfileDomainModel>

    suspend fun changePassword(password: String): ViewState<Void>

    suspend fun resetPassword(email: String): ViewState<Void>

    suspend fun getKakaoAccount(accessToken: String): ViewState<KakaoAccountDomainModel>

    fun getLoginType(): Int
}