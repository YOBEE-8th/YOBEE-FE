package com.ssafy.data.remote.datasource.recipe.dto

import java.util.*

data class RecipeReviewResponseDto(
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