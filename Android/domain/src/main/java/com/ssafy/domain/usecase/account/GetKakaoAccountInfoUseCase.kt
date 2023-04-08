package com.ssafy.domain.usecase.account

import com.ssafy.domain.repository.AccountRepository
import javax.inject.Inject

class GetKakaoAccountInfoUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(accessToken: String) =
        accountRepository.getKakaoAccount(accessToken)
}