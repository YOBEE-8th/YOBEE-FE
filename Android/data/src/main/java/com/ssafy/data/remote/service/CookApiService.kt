package com.ssafy.data.remote.service

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.cook.dto.CookProgressResponseDto
import retrofit2.http.GET
import retrofit2.http.Path


internal interface CookApiService {
    @GET("recipe/{recipeId}/")
    suspend fun getCookProgress(@Path("recipeId") recipeId: Int): BaseResponse<List<CookProgressResponseDto>>
}