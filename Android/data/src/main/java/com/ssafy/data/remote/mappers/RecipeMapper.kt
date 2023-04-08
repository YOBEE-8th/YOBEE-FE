package com.ssafy.data.remote.mappers

import com.ssafy.data.remote.datasource.recipe.dto.RecipeDetailResponseDto
import com.ssafy.data.remote.datasource.recipe.dto.RecipeIngredientResponseDto
import com.ssafy.data.remote.datasource.recipe.dto.RecipeResponseDto
import com.ssafy.data.remote.datasource.recipe.dto.RecipeReviewResponseDto
import com.ssafy.domain.model.recipe.RecipeDetailDomainModel
import com.ssafy.domain.model.recipe.RecipeDomainModel
import com.ssafy.domain.model.recipe.RecipeIngredientDomainModel
import com.ssafy.domain.model.recipe.RecipeReviewDomainModel

internal fun RecipeResponseDto.toDomainModel() = RecipeDomainModel(
    recipeId = this.recipeId,
    imageUrl = this.imageUrl,
    title = this.title,
    isAI = this.isAI,
    likeCount = this.likeCount,
    isLike = this.isLike,
    difficulty = this.level
)

internal fun RecipeIngredientResponseDto.toDomainModel() = RecipeIngredientDomainModel(
    ingredient = this.ingredient
)

internal fun RecipeDetailResponseDto.toDomainModel() = RecipeDetailDomainModel(
    title = this.title,
    image = this.image,
    time = this.time,
    level = this.level,
    servings = this.servings,
    isAI = this.isAI,
    category = this.category,
    likeCnt = this.likeCnt,
    isLike = this.isLike,
    hashtag = this.hashTag,
    reviewCnt = this.reviewCnt
)

internal fun RecipeReviewResponseDto.toDomainModel() = RecipeReviewDomainModel(
    reviewId = this.reviewId,
    profileImg = this.profileImg,
    nickname = this.nickname,
    createdAt = this.createdAt,
    reviewImg = this.reviewImg,
    content = this.content,
    likeCnt = this.likeCnt,
    isLike = this.isLike,
    isMine = this.isMine
)