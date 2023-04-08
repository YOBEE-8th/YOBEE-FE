package com.ssafy.data.remote.datasource.user.dto

import java.util.*

data class ReviewByDateResponseDto(
    val reviewId: Long,
    val reviewImage: String,
    val title: String,
    val isCompleted: Boolean,
    val createdAt: Date,
)