package com.ssafy.domain.usecase.account

import com.ssafy.domain.repository.AccountRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(email: String, password: String, fcmToken: String) =
        accountRepository.login(email, password, fcmToken)
}