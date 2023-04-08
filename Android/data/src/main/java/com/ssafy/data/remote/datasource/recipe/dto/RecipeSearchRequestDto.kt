package com.ssafy.data.remote.datasource.recipe.dto

data class RecipeSearchRequestDto(
    val keyword: String,
    val isAI: Boolean,
    val order: Boolean,
    val sort: Int,
)
