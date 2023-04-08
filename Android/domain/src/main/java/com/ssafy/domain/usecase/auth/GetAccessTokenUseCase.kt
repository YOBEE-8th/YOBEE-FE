package com.ssafy.domain.usecase.auth

import com.ssafy.domain.repository.AuthRepository
import javax.inject.Inject

class GetAccessTokenUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke() = authRepository.getAccessToken()
}