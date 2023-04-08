package com.ssafy.data.remote.datasource.cook

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.cook.dto.CookProgressResponseDto

internal interface CookDataSource {
    suspend fun getCookProgressList(recipeId: Int): BaseResponse<List<CookProgressResponseDto>>
}