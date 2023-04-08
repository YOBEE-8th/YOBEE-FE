package com.ssafy.domain.usecase.account

import com.ssafy.domain.repository.AccountRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(private val accountRepository: AccountRepository) {
    suspend operator fun invoke(
        profileImage: MultipartBody.Part?,
        nickname: RequestBody,
    ) = accountRepository.updateProfile(profileImage, nickname)
}