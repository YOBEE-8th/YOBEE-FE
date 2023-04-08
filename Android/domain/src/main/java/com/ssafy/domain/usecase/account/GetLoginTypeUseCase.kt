package com.ssafy.domain.usecase.account

import com.ssafy.domain.repository.AccountRepository
import javax.inject.Inject

class GetLoginTypeUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    operator fun invoke() = accountRepository.getLoginType()
}