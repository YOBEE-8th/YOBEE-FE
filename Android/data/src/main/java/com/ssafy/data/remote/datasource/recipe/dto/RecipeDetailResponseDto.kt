package com.ssafy.data.remote.datasource.recipe.dto

internal data class RecipeDetailResponseDto(
    val title: String,
    val image: String,
    val time: String,
    val level: Int,
    val servings: String,
    val isAI: Boolean,
    val category: String,
    val likeCnt: Int,
    val isLike: Boolean,
    val hashTag: List<String>,
    val reviewCnt: Long
)