package com.ssafy.yobee.ui.recipe.model

import com.ssafy.common.CommonUtils
import com.ssafy.domain.model.recipe.RecipeReviewDomainModel

data class ReviewModel(
    val reviewId: Int,
    val profileImg: String,
    val nickname: String,
    val createdAt: String,
    val reviewImg: String,
    val content: String,
    var likeCnt: Int,
    var isLike: Boolean,
    var isMine: Boolean,
)

fun RecipeReviewDomainModel.toReviewModel() = ReviewModel(
    reviewId = this.reviewId,
    profileImg = this.profileImg,
    nickname = this.nickname,
    createdAt = CommonUtils.dateToString(this.createdAt),
    reviewImg = this.reviewImg,
    content = this.content,
    likeCnt = this.likeCnt,
    isLike = this.isLike,
    isMine = this.isMine
)