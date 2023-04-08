package com.ssafy.data.remote.service

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.recipe.dto.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface RecipeApiService {
    @POST("recipe/list/v2")
    suspend fun getRecipeList(@Body recipeRequestDto: RecipeRequestDto): BaseResponse<List<RecipeResponseDto>>

    @GET("recipe/{recipeId}/ingredient")
    suspend fun getRecipeIngredient(@Path("recipeId") recipeId: Int): BaseResponse<RecipeIngredientResponseDto>

    @GET("recipe/{recipeId}/intro")
    suspend fun getRecipeDetail(@Path("recipeId") recipeId: Int): BaseResponse<RecipeDetailResponseDto>

    @GET("recipe/{recipeId}/review")
    suspend fun getRecipeReview(@Path("recipeId") recipeId: Int): BaseResponse<List<RecipeReviewResponseDto>>

    @POST("recipe/search")
    suspend fun getSearchedRecipeList(@Body recipeSearchRequestDto: RecipeSearchRequestDto): BaseResponse<List<RecipeResponseDto>>

    @GET("recipe/{recipeId}/like")
    suspend fun addLikeRecipe(@Path("recipeId") recipeId: Int): BaseResponse<Any>
}