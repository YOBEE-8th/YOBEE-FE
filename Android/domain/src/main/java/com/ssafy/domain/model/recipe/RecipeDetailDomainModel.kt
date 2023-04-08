package com.ssafy.domain.model.recipe

data class RecipeDetailDomainModel(
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