package com.ssafy.data.remote.repository

import com.ssafy.data.local.prefs.AccountSharePreference
import com.ssafy.data.local.prefs.AuthSharePreference
import com.ssafy.data.remote.datasource.auth.AuthRemoteDataSource
import com.ssafy.data.remote.datasource.auth.dto.AuthRequestDto
import com.ssafy.data.remote.datasource.auth.dto.AuthResponseDto
import com.ssafy.data.remote.mappers.toDomainModel
import com.ssafy.data.util.ErrorMessage
import com.ssafy.domain.model.auth.AuthDomainModel
import com.ssafy.domain.repository.AuthRepository
import com.ssafy.domain.utils.ViewState
import javax.inject.Inject

private const val TAG = "AuthRepositoryImpl_요비"

internal class AuthRepositoryImpl @Inject constructor(
    private val authSharePreference: AuthSharePreference,
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val accountSharePreference: AccountSharePreference,
) : AuthRepository {

    override fun getAccessToken() = authSharePreference.accessToken

    override fun getRefreshToken() = authSharePreference.refreshToken

    override suspend fun validateToken(
        accessToken: String,
        refreshToken: String,
    ): ViewState<AuthDomainModel> {
        return try {
            val request = AuthRequestDto(accessToken, refreshToken)
            val response = authRemoteDataSource.validateToken(request)
            if (response.status == 200) {
                setTokenInfo(response.data!!)
                ViewState.Success(response.message, response.data.toDomainModel())
            } else {
                clearUserInfo()
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(ErrorMessage.ERROR_MESSAGE, null)
        }
    }

    private fun setTokenInfo(authResponseDto: AuthResponseDto) {
        with(authSharePreference) {
            accessToken = authResponseDto.accessToken
            refreshToken = authResponseDto.refreshToken
        }
    }

    private fun clearUserInfo() {
        authSharePreference.clearAuthPreference()
        accountSharePreference.clearAccountPreference()
    }
}