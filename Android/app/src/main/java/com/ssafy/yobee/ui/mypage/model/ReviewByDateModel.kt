package com.ssafy.yobee.ui.mypage.model

import com.ssafy.domain.model.user.ReviewByDateDomainModel
import com.ssafy.yobee.util.extension.getReviewDateType

data class ReviewByDateModel(
    val reviewId: Long,
    val reviewImg: String,
    val title: String,
    val isCompleted: Boolean,
    val createdAt: String,
)

fun ReviewByDateDomainModel.toReviewByDateModel() = ReviewByDateModel(
    reviewId = this.reviewId,
    reviewImg = this.reviewImage,
    title = this.title,
    isCompleted = this.isCompleted,
    createdAt = getReviewDateType(this.createdAt)
)