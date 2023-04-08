package com.ssafy.data.remote.datasource.voice

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.voice.dto.VoiceRequestDto
import com.ssafy.data.remote.datasource.voice.dto.VoiceResponseDto
import com.ssafy.data.remote.service.VoiceApiService
import javax.inject.Inject

internal class VoiceRemoteDataSourceImpl @Inject constructor(private val voiceApiService: VoiceApiService) :
    VoiceRemoteDataSource {
    override suspend fun getAnswer(voiceRequestDto: VoiceRequestDto): BaseResponse<VoiceResponseDto> {
        return voiceApiService.getAnswer(voiceRequestDto)
    }
}