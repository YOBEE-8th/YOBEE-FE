package com.ssafy.domain.usecase.account

import com.ssafy.domain.repository.AccountRepository
import javax.inject.Inject

class ValidateNickNameUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(nickname: String) = accountRepository.validateNickname(nickname)
}