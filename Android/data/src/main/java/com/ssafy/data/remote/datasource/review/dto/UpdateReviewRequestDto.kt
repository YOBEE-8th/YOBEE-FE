package com.ssafy.data.remote.datasource.review.dto

data class UpdateReviewRequestDto(
    val reviewId: Int,
    val content: String,
)