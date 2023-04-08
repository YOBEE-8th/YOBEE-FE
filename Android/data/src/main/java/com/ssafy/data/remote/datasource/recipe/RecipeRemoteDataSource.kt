package com.ssafy.data.remote.datasource.recipe

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.recipe.dto.*

internal interface RecipeRemoteDataSource {
    suspend fun getRecipeList(
        recipeRequestDto: RecipeRequestDto,
    ): BaseResponse<List<RecipeResponseDto>>

    suspend fun getRecipeIngredient(recipeId: Int): BaseResponse<RecipeIngredientResponseDto>

    suspend fun getRecipeDetail(recipeId: Int): BaseResponse<RecipeDetailResponseDto>

    suspend fun getRecipeReview(recipeId: Int): BaseResponse<List<RecipeReviewResponseDto>>

    suspend fun getSearchedRecipeList(
        recipeSearchRequestDto: RecipeSearchRequestDto,
    ): BaseResponse<List<RecipeResponseDto>>

    suspend fun addLikeRecipe(recipeId: Int): BaseResponse<Any>
}