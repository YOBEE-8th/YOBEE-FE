package com.ssafy.data.remote.datasource.recipe.dto

data class RecipeRequestDto(
    val category: String,
    val sort: Int,
    val order: Boolean,
    val isAI: Boolean,
    val page: Int,
)