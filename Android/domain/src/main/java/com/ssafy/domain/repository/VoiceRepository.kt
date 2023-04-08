package com.ssafy.domain.repository

import com.ssafy.domain.model.voice.VoiceDomainModel
import com.ssafy.domain.utils.ViewState

interface VoiceRepository {
    suspend fun getAnswer(
        recipeId: Int, message: String,
    ): ViewState<VoiceDomainModel>
}