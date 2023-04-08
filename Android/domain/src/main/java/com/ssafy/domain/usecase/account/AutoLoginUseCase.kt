package com.ssafy.domain.usecase.account

import com.ssafy.domain.repository.AccountRepository
import javax.inject.Inject

class AutoLoginUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(email: String, password: String, fcmToken: String) =
        accountRepository.autoLogin(email, password, fcmToken)
}