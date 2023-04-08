package com.ssafy.data.remote.service

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.recipe.dto.RecipeResponseDto
import com.ssafy.data.remote.datasource.user.dto.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

internal interface UserApiService {
    @GET("user/exp")
    suspend fun getExp(): BaseResponse<ExpResponseDto>

    @POST("user/exp")
    suspend fun increaseExp(@Body increaseExpRequestDto: IncreaseExpRequestDto): BaseResponse<IncreaseExpResponseDto>

    @GET("user/review/recipe")
    suspend fun getReviewByRecipe(): BaseResponse<List<ReviewByRecipeResponseDto>>

    @POST("user/review/history")
    suspend fun getReviewByDate(@Body reviewByDateRequestDto: ReviewByDateRequestDto): BaseResponse<List<ReviewByDateResponseDto>>

    @GET("user/like/list")
    suspend fun getLikeRecipeList(): BaseResponse<List<RecipeResponseDto>>
}