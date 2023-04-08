package com.ssafy.data.remote.datasource.recipe

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.recipe.dto.*
import com.ssafy.data.remote.service.RecipeApiService
import javax.inject.Inject

internal class RecipeRemoteDataSourceImpl @Inject constructor(private val recipeApiService: RecipeApiService) :
    RecipeRemoteDataSource {
    override suspend fun getRecipeList(
        recipeRequestDto: RecipeRequestDto,
    ): BaseResponse<List<RecipeResponseDto>> {
        return recipeApiService.getRecipeList(recipeRequestDto)
    }

    override suspend fun getRecipeIngredient(recipeId: Int): BaseResponse<RecipeIngredientResponseDto> {
        return recipeApiService.getRecipeIngredient(recipeId)
    }

    override suspend fun getRecipeDetail(recipeId: Int): BaseResponse<RecipeDetailResponseDto> {
        return recipeApiService.getRecipeDetail(recipeId)
    }

    override suspend fun getRecipeReview(recipeId: Int): BaseResponse<List<RecipeReviewResponseDto>> {
        return recipeApiService.getRecipeReview(recipeId)
    }

    override suspend fun getSearchedRecipeList(recipeSearchRequestDto: RecipeSearchRequestDto): BaseResponse<List<RecipeResponseDto>> =
        recipeApiService.getSearchedRecipeList(recipeSearchRequestDto)

    override suspend fun addLikeRecipe(recipeId: Int): BaseResponse<Any> =
        recipeApiService.addLikeRecipe(recipeId)
}