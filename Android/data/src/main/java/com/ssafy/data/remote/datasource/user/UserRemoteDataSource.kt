package com.ssafy.data.remote.datasource.user

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.recipe.dto.RecipeResponseDto
import com.ssafy.data.remote.datasource.user.dto.*

internal interface UserRemoteDataSource {
    suspend fun getExp(): BaseResponse<ExpResponseDto>
    suspend fun increaseExp(increaseExpRequestDto: IncreaseExpRequestDto): BaseResponse<IncreaseExpResponseDto>
    suspend fun getLikeRecipeList(): BaseResponse<List<RecipeResponseDto>>
    suspend fun getReviewByRecipe(): BaseResponse<List<ReviewByRecipeResponseDto>>
    suspend fun getReviewByDate(reviewByDateRequestDto: ReviewByDateRequestDto): BaseResponse<List<ReviewByDateResponseDto>>
}