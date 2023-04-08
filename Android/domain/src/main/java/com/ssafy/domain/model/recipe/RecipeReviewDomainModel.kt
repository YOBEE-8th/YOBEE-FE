package com.ssafy.domain.model.recipe

import java.util.*

data class RecipeReviewDomainModel(
    val reviewId: Int,
    val profileImg: String,
    val nickname: String,
    val createdAt: Date,
    val reviewImg: String,
    val content: String,
    val likeCnt: Int,
    val isLike: Boolean,
    val isMine: Boolean,
)