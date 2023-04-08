package com.ssafy.domain.usecase.fcm

import com.ssafy.domain.repository.FcmRepository
import javax.inject.Inject

class GetFcmTokenUseCase @Inject constructor(private val fcmRepository: FcmRepository) {
    operator fun invoke() = fcmRepository.getFcmToken()
}