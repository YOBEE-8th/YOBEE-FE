package com.ssafy.domain.model.user

import java.util.*

data class ReviewByDateDomainModel(
    val reviewId: Long,
    val reviewImage: String,
    val title: String,
    val isCompleted: Boolean,
    val createdAt: Date,
)