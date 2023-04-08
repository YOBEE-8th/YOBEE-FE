package com.ssafy.data.remote.datasource.user.dto

data class ReviewByRecipeResponseDto(
    val recipeId: Long,
    val recipeImage: String,
    val title: String,
    val reviewCnt: Int,
)