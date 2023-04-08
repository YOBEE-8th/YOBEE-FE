package com.ssafy.domain.usecase.account

import com.ssafy.domain.repository.AccountRepository
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(email: String) = accountRepository.validateEmail(email)
}