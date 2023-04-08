package com.ssafy.domain.repository

import com.ssafy.domain.model.auth.AuthDomainModel
import com.ssafy.domain.utils.ViewState

interface AuthRepository {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    suspend fun validateToken(accessToken: String, refreshToken: String): ViewState<AuthDomainModel>
}