package com.ssafy.domain.model.user

data class ReviewByRecipeDomainModel(
    val recipeId: Long,
    val recipeImage: String,
    val title: String,
    val reviewCnt: Int,
)