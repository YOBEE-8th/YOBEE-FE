package com.ssafy.yobee.ui.mypage.model

import com.ssafy.domain.model.user.ReviewByRecipeDomainModel

data class ReviewByRecipeModel(
    val recipeId: Long,
    val img: String,
    val title: String,
    val reviewCnt: Int,
)

fun ReviewByRecipeDomainModel.toReviewByRecipeModel() = ReviewByRecipeModel(
    recipeId = this.recipeId,
    img = this.recipeImage,
    title = this.title,
    reviewCnt = this.reviewCnt
)