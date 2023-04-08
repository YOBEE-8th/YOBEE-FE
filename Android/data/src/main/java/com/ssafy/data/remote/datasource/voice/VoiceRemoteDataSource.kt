package com.ssafy.data.remote.datasource.voice

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.voice.dto.VoiceRequestDto
import com.ssafy.data.remote.datasource.voice.dto.VoiceResponseDto

internal interface VoiceRemoteDataSource {
    suspend fun getAnswer(voiceRequestDto: VoiceRequestDto): BaseResponse<VoiceResponseDto>
}