package com.ssafy.data.remote.repository

import com.ssafy.data.remote.datasource.voice.VoiceRemoteDataSource
import com.ssafy.data.remote.datasource.voice.dto.VoiceRequestDto
import com.ssafy.data.remote.mappers.toDomainModel
import com.ssafy.data.util.ErrorMessage
import com.ssafy.domain.model.voice.VoiceDomainModel
import com.ssafy.domain.repository.VoiceRepository
import com.ssafy.domain.utils.ViewState
import javax.inject.Inject

private const val TAG = "VoiceRepositoryImpl_요비"

internal class VoiceRepositoryImpl @Inject constructor(
    private val voiceRemoteDataSource: VoiceRemoteDataSource,
) : VoiceRepository {

    override suspend fun getAnswer(recipeId: Int, message: String): ViewState<VoiceDomainModel> {
        return try {
            val request = VoiceRequestDto(recipeId, message)
            val response = voiceRemoteDataSource.getAnswer(request)

            if (response.status == 200) {
                ViewState.Success(response.message, response.data!!.toDomainModel())
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(ErrorMessage.ERROR_MESSAGE, null)
        }
    }
}