package com.ssafy.data.remote.datasource.recipe.dto

import com.google.gson.annotations.SerializedName

data class RecipeResponseDto(
    @SerializedName("recipeId")
    val recipeId: Int,
    @SerializedName("imgUrl")
    val imageUrl: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("isAI")
    val isAI: Boolean,
    @SerializedName("likeCnt")
    val likeCount: Int,
    @SerializedName("isLike")
    val isLike: Boolean,
    @SerializedName("level")
    val level: Int,
)