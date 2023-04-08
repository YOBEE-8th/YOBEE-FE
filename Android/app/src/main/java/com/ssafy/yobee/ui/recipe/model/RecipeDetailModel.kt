package com.ssafy.yobee.ui.recipe.model

import com.ssafy.domain.model.recipe.RecipeDetailDomainModel

data class RecipeDetailModel(
    val title: String,
    val image: String,
    val time: String,
    val level: Int,
    val servings: String,
    val isAI: Boolean,
    val category: String,
    val likeCnt: Int,
    val isLike: Boolean,
    val hashtag: List<String>,
    val reviewCnt: Long
)

fun RecipeDetailDomainModel.toRecipeDetailModel() = RecipeDetailModel(
    title = this.title,
    image = this.image,
    time = this.time,
    level = this.level,
    servings = this.servings,
    isAI = this.isAI,
    category = this.category,
    likeCnt = this.likeCnt,
    isLike = this.isLike,
    hashtag = this.hashtag,
    reviewCnt = this.reviewCnt
)