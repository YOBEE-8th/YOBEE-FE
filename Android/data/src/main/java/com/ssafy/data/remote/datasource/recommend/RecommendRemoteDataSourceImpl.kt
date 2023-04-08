package com.ssafy.data.remote.datasource.recommend

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.recommend.dto.RecommendResponseDto
import com.ssafy.data.remote.service.RecommendApiService
import javax.inject.Inject

internal class RecommendRemoteDataSourceImpl @Inject constructor(private val recommendApiService: RecommendApiService) :
    RecommendRemoteDataSource {
    override suspend fun getRecommendRecipe(): BaseResponse<List<RecommendResponseDto>> =
        recommendApiService.getRecommendRecipe()
}