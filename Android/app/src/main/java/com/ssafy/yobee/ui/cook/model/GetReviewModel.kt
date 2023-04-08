package com.ssafy.yobee.ui.cook.model

import com.ssafy.domain.model.review.GetReviewDomainModel

data class GetReviewModel(
    val reviewImage: String,
    val content: String?,
)

fun GetReviewDomainModel.toGetReviewModel() = GetReviewModel(
    reviewImage = this.reviewImage,
    content = this.content
)