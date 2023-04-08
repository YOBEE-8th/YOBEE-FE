package com.ssafy.domain.usecase.account

import com.ssafy.domain.repository.AccountRepository
import javax.inject.Inject

class ValidateNickNameAuthUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(nickname: String) = accountRepository.validateNicknameAuth(nickname)
}