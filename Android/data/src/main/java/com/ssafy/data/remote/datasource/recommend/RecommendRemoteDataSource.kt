package com.ssafy.data.remote.datasource.recommend

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.recommend.dto.RecommendResponseDto

internal interface RecommendRemoteDataSource {
    suspend fun getRecommendRecipe(): BaseResponse<List<RecommendResponseDto>>
}