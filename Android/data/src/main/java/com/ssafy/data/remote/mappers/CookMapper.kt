package com.ssafy.data.remote.mappers

import com.ssafy.data.remote.datasource.cook.dto.CookProgressResponseDto
import com.ssafy.domain.model.cook.CookProgressStepDomainModel

internal fun CookProgressResponseDto.toDomainModel() = CookProgressStepDomainModel(
    this.fire,
    this.timer,
    this.stepImg,
    this.description
)