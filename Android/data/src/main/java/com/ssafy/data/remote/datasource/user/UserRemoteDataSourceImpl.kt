package com.ssafy.data.remote.datasource.user

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.recipe.dto.RecipeResponseDto
import com.ssafy.data.remote.datasource.user.dto.*
import com.ssafy.data.remote.service.UserApiService
import javax.inject.Inject

private const val TAG = "UserRemoteDataSourceImp_요비"

internal class UserRemoteDataSourceImpl @Inject constructor(private val userApiService: UserApiService) :
    UserRemoteDataSource {
    override suspend fun getExp(): BaseResponse<ExpResponseDto> = userApiService.getExp()


    override suspend fun increaseExp(increaseExpRequestDto: IncreaseExpRequestDto): BaseResponse<IncreaseExpResponseDto> =
        userApiService.increaseExp(increaseExpRequestDto)


    override suspend fun getLikeRecipeList(): BaseResponse<List<RecipeResponseDto>> =
        userApiService.getLikeRecipeList()

    override suspend fun getReviewByRecipe(): BaseResponse<List<ReviewByRecipeResponseDto>> =
        userApiService.getReviewByRecipe()


    override suspend fun getReviewByDate(reviewByDateRequestDto: ReviewByDateRequestDto): BaseResponse<List<ReviewByDateResponseDto>> =
        userApiService.getReviewByDate(reviewByDateRequestDto)
}