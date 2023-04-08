package com.ssafy.domain.usecase.account

import com.ssafy.domain.repository.AccountRepository
import javax.inject.Inject

class SocialLoginUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(email: String, type: Int, fcmToken: String) =
        accountRepository.socialLogin(email, type, fcmToken)
}