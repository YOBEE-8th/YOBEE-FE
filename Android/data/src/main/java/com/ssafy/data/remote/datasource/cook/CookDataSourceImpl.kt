package com.ssafy.data.remote.datasource.cook

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.cook.dto.CookProgressResponseDto
import com.ssafy.data.remote.service.CookApiService
import javax.inject.Inject

internal class CookDataSourceImpl @Inject constructor(private val cookApiService: CookApiService) :
    CookDataSource {
    override suspend fun getCookProgressList(recipeId: Int): BaseResponse<List<CookProgressResponseDto>> {
        return cookApiService.getCookProgress(recipeId)
    }
}