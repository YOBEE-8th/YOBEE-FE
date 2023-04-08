package com.ssafy.data.remote.service

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.voice.dto.VoiceRequestDto
import com.ssafy.data.remote.datasource.voice.dto.VoiceResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

internal interface VoiceApiService {
    @POST("voice")
    suspend fun getAnswer(@Body voiceRequestDto: VoiceRequestDto): BaseResponse<VoiceResponseDto>
}