package com.ssafy.domain.usecase.voice

import com.ssafy.domain.repository.VoiceRepository
import javax.inject.Inject

class GetAnswerUseCase @Inject constructor(private val voiceRepository: VoiceRepository) {
    suspend operator fun invoke(recipeId: Int, message: String) =
        voiceRepository.getAnswer(recipeId, message)
}