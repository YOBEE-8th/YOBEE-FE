package com.ssafy.data.remote.mappers

import com.ssafy.data.remote.datasource.voice.dto.VoiceResponseDto
import com.ssafy.domain.model.voice.VoiceDomainModel

internal fun VoiceResponseDto.toDomainModel() = VoiceDomainModel(
    type = this.type,
    text = this.text
)