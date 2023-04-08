package com.ssafy.data.remote.service

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.recommend.dto.RecommendResponseDto
import retrofit2.http.GET

internal interface RecommendApiService {
    @GET("recipe/recommend")
    suspend fun getRecommendRecipe(): BaseResponse<List<RecommendResponseDto>>
}