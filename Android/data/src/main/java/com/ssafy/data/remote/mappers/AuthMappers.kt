package com.ssafy.data.remote.mappers

import com.ssafy.data.remote.datasource.auth.dto.AuthResponseDto
import com.ssafy.domain.model.auth.AuthDomainModel

internal fun AuthResponseDto.toDomainModel() = AuthDomainModel(
    accessToken = this.accessToken!!,
    refreshToken = this.refreshToken!!,
)

