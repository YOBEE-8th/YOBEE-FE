package com.ssafy.data.remote.mappers

import com.ssafy.data.remote.datasource.recommend.dto.RecommendResponseDto
import com.ssafy.domain.model.recommend.RecommendDomainModel

fun RecommendResponseDto.toDomainModel() = RecommendDomainModel(
    recipeId = recipeId,
    image = image,
    title = title
)