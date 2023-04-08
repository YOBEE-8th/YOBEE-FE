package com.ssafy.data.remote.repository

import android.util.Log
import com.ssafy.data.local.prefs.AccountSharePreference
import com.ssafy.data.local.prefs.AuthSharePreference
import com.ssafy.data.remote.datasource.account.AccountRemoteDataSource
import com.ssafy.data.remote.datasource.account.dto.*
import com.ssafy.data.remote.mappers.toDomainModel
import com.ssafy.data.remote.mappers.toLogoutDomainModel
import com.ssafy.data.remote.mappers.toProfileDomainModel
import com.ssafy.data.remote.mappers.toWithdrawalModel
import com.ssafy.data.util.ErrorMessage
import com.ssafy.domain.model.account.*
import com.ssafy.domain.repository.AccountRepository
import com.ssafy.domain.utils.ViewState
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

private const val TAG = "AccountRepositoryImpl_요비"

internal class AccountRepositoryImpl @Inject constructor(
    private val accountRemoteDataSource: AccountRemoteDataSource,
    private val authSharePreference: AuthSharePreference,
    private val accountSharePreference: AccountSharePreference,
) : AccountRepository {
    override suspend fun login(
        email: String,
        password: String,
        fcmToken: String,
    ): ViewState<UserInfoDomainModel> {
        return try {
            val request = LoginRequestDto(email, password, fcmToken)
            val response = accountRemoteDataSource.login(request)
            if (response.status == 200) {
                setUserInfoLogin(response.data!!)
                ViewState.Success(response.message, response.data.toDomainModel())
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(ErrorMessage.ERROR_MESSAGE, null)
        }
    }

    override suspend fun validateEmail(email: String): ViewState<Void> {
        return try {
            val request = ValidateEmailRequestDto(email)
            val response = accountRemoteDataSource.validateEmail(request)

            if (response.status == 200) {
                ViewState.Success(response.message, null)
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(ErrorMessage.ERROR_MESSAGE, null)
        }
    }

    override suspend fun checkVerificationCode(verificationCode: String): ViewState<Void> {
        return try {
            val request = CheckVerificationCodeRequestDto(verificationCode)
            val response = accountRemoteDataSource.checkVerificationCode(request)
            if (response.status == 200) {
                ViewState.Success(response.message, null)
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(ErrorMessage.ERROR_MESSAGE, null)
        }
    }

    override suspend fun validateNickname(nickname: String): ViewState<Void> {
        return try {
            val request = ValidateNicknameRequestDto(nickname)
            val response = accountRemoteDataSource.validateNickname(request)

            if (response.status == 200) {
                ViewState.Success(response.message, null)
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(ErrorMessage.ERROR_MESSAGE, null)
        }
    }

    override suspend fun joinAccount(
        profileImage: MultipartBody.Part?,
        join: HashMap<String, RequestBody>,
    ): ViewState<AccountnfoDomainModel> {
        return try {
            val response = accountRemoteDataSource.joinAccount(profileImage, join)
            if (response.status == 200) {
                setUserInfo(response.data!!)
                ViewState.Success(response.message, response.data.toDomainModel())
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(ErrorMessage.ERROR_MESSAGE, null)
        }
    }

    override suspend fun autoLogin(
        email: String,
        password: String,
        fcmToken: String,
    ): ViewState<AccountnfoDomainModel> {
        return try {
            val request = AutoLoginRequestDto(email, password, fcmToken)
            val response = accountRemoteDataSource.autoLogin(request)
            if (response.status == 200) {
                setUserInfo(response.data!!)
                ViewState.Success(response.message, response.data.toDomainModel())
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(ErrorMessage.ERROR_MESSAGE, null)
        }
    }

    override suspend fun socialLogin(
        email: String,
        type: Int,
        fcmToken: String,
    ): ViewState<AccountnfoDomainModel> {
        return try {
            Log.d("tag", "socialLogin : $email, $type, $fcmToken")
            val request = SocialLoginRequestDto(email, type, fcmToken)
            val response = accountRemoteDataSource.socialLogin(request)
            if (response.status == 200) {
                setUserInfo(response.data!!)
                ViewState.Success(response.message, response.data.toDomainModel())
            } else if (response.status == 700) {
                ViewState.Error(response.status.toString(), null)
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(ErrorMessage.ERROR_MESSAGE, null)
        }
    }

    override fun autoLoginCheck(): String? {
        if (accountSharePreference.autoLogin == "0") {
            accountSharePreference.clearAccountPreference()
            authSharePreference.clearAuthPreference()
        }
        return accountSharePreference.autoLogin
    }

    override suspend fun logout(): ViewState<LogoutDomainModel> {
        return try {
            val response = accountRemoteDataSource.logout()
            if (response.status == 200) {
                authSharePreference.clearAuthPreference()
                accountSharePreference.clearAccountPreference()
                ViewState.Success(response.message, response.data!!.toLogoutDomainModel())
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(ErrorMessage.ERROR_MESSAGE, null)
        }
    }

    override suspend fun withdrawal(): ViewState<WithdrawalDomainModel> {
        return try {
            val response = accountRemoteDataSource.withdrawal()
            if (response.status == 200) {
                authSharePreference.clearAuthPreference()
                accountSharePreference.clearAccountPreference()
                ViewState.Success(response.message, response.data!!.toWithdrawalModel())
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(ErrorMessage.ERROR_MESSAGE, null)
        }
    }

    override fun getProfile(): ProfileDomainModel {
        return ProfileDomainModel(
            accountSharePreference.nickname!!,
            accountSharePreference.profileImage
        )
    }

    override suspend fun validateNicknameAuth(nickname: String): ViewState<Void> {
        return try {
            val request = ValidateNicknameRequestDto(nickname)
            val response = accountRemoteDataSource.validateNicknameAuth(request)

            if (response.status == 200) {
                ViewState.Success(response.message, null)
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(ErrorMessage.ERROR_MESSAGE, null)
        }
    }

    override suspend fun updateProfile(
        profileImage: MultipartBody.Part?,
        nickname: RequestBody,
    ): ViewState<ProfileDomainModel> {
        return try {
            val response = accountRemoteDataSource.updateProfile(profileImage, nickname)
            if (response.status == 200) {
                setProfileInfo(response.data!!)
                ViewState.Success(response.message, response.data.toProfileDomainModel())
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(ErrorMessage.ERROR_MESSAGE, null)
        }
    }

    override suspend fun changePassword(password: String): ViewState<Void> {
        return try {
            val response =
                accountRemoteDataSource.changePassword(ChangePasswordRequestDto(password))
            if (response.status == 200) {
                ViewState.Success(response.message, null)
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(ErrorMessage.ERROR_MESSAGE, null)
        }
    }

    override suspend fun resetPassword(email: String): ViewState<Void> {
        return try {
            val response = accountRemoteDataSource.resetPassword(ResetPasswordRequestDto(email))
            if (response.status == 200) {
                ViewState.Success(response.message, null)
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(ErrorMessage.ERROR_MESSAGE, null)
        }
    }

    override suspend fun getKakaoAccount(accessToken: String): ViewState<KakaoAccountDomainModel> {
        authSharePreference.kakaoAccessToken = accessToken
        return try {
            accountRemoteDataSource.getKakaoAccountInfo().run {
                Log.d(TAG, "getKakaoAccount: $this")
                if (this != null) {
                    ViewState.Success(
                        "success",
                        KakaoAccountDomainModel(
                            this.id,
                            this.properties?.nickname ?: "",
                            this.properties?.profile_image ?: "",
                            this.kakaoAccount?.email ?: ""
                        )
                    )
                } else {
                    ViewState.Error("error", null)
                }
            }
        } catch (e: Exception) {
            ViewState.Error(e.message, null)
        }
    }

    override fun getLoginType(): Int = accountSharePreference.type!!.toInt()

    private fun setUserInfo(accountResponseDto: AccountResponseDto) {
        with(authSharePreference) {
            accessToken = accountResponseDto.accessToken
            refreshToken = accountResponseDto.refreshToken
        }
        with(accountSharePreference) {
            nickname = accountResponseDto.nickName
            profileImage = accountResponseDto.profileImage
            type = accountResponseDto.type.toString()
            autoLogin = "1"
        }
    }

    private fun setProfileInfo(accountResponseDto: AccountResponseDto) {
        with(accountSharePreference) {
            nickname = accountResponseDto.nickName
            profileImage = accountResponseDto.profileImage
        }
    }

    private fun setUserInfoLogin(userResponseDto: UserResponseDto) {
        with(authSharePreference) {
            accessToken = userResponseDto.accessToken
            refreshToken = userResponseDto.refreshToken
        }
        with(accountSharePreference) {
            nickname = userResponseDto.nickName
            profileImage = userResponseDto.profileImage
            type = userResponseDto.type.toString()
            autoLogin = "0"
        }
    }

    override suspend fun getNickname(): ViewState<String> {
        val response = accountSharePreference.nickname
        return ViewState.Success("닉네임 불러오기 성공", response)
    }

    override suspend fun getProfileImage(): ViewState<String> {
        val response = accountSharePreference.profileImage
        return ViewState.Success("프로필 이미지 불러오기 성공", response)
    }
}