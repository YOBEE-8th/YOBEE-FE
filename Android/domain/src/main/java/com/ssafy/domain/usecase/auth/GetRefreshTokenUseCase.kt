package com.ssafy.domain.usecase.auth

import com.ssafy.domain.repository.AuthRepository
import javax.inject.Inject

class GetRefreshTokenUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke() = authRepository.getRefreshToken()
}