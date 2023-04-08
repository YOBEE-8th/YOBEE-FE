package com.ssafy.domain.usecase.account

import com.ssafy.domain.repository.AccountRepository
import javax.inject.Inject

class CheckVerificationCodeUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(verificationCode: String) =
        accountRepository.checkVerificationCode(verificationCode)
}