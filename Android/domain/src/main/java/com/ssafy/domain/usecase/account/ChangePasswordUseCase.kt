package com.ssafy.domain.usecase.account

import com.ssafy.domain.repository.AccountRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(password: String) = accountRepository.changePassword(password)
}