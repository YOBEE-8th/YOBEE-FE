package com.ssafy.domain.usecase.auth

import com.ssafy.domain.repository.AuthRepository
import javax.inject.Inject

class ValidateTokenUseCase @Inject constructor(private val authRepository: AuthRepository) {
    suspend operator fun invoke(accessToken: String, refreshToken: String) =
        authRepository.validateToken(accessToken, refreshToken)
}