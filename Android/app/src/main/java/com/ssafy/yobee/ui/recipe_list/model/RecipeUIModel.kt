package com.ssafy.yobee.ui.recipe_list.model

import com.ssafy.domain.model.recipe.RecipeDomainModel

data class RecipeUIModel(
    val title: String,
    val imageUrl: String,
    val difficulty: Int,
    val likeCount: Int,
    val isLike: Boolean,
    val isAI: Boolean,
)

fun RecipeDomainModel.toRecipeUIModel() = RecipeUIModel(
    title = this.title,
    imageUrl = this.imageUrl,
    difficulty = this.difficulty,
    likeCount = this.likeCount,
    isLike = this.isLike,
    isAI = this.isAI
)