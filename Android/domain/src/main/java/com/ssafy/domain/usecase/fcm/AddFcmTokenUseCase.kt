package com.ssafy.domain.usecase.fcm

import com.ssafy.domain.repository.FcmRepository
import javax.inject.Inject

class AddFcmTokenUseCase @Inject constructor(private val fcmRepository: FcmRepository) {
    operator fun invoke(token: String) = fcmRepository.addFcmToken(token)
}