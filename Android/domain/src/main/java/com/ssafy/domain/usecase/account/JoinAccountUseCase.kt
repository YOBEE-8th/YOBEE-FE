package com.ssafy.domain.usecase.account

import com.ssafy.domain.repository.AccountRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class JoinAccountUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(
        profileImage: MultipartBody.Part?,
        join: HashMap<String, RequestBody>,
    ) = accountRepository.joinAccount(profileImage, join)
}