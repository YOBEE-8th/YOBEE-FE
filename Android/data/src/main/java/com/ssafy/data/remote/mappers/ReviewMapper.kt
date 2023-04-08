package com.ssafy.data.remote.mappers

import com.ssafy.data.remote.datasource.review.dto.CreateReviewResponseDto
import com.ssafy.data.remote.datasource.review.dto.GetReviewResponseDto
import com.ssafy.domain.model.review.CreateReviewDomainModel
import com.ssafy.domain.model.review.GetReviewDomainModel

internal fun CreateReviewResponseDto.toDomainModel() = CreateReviewDomainModel(
    reviewId = this.reviewId
)

internal fun GetReviewResponseDto.toDomainModel() = GetReviewDomainModel(
    reviewImage = this.reviewImage,
    content = this.content
)