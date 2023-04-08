package com.ssafy.domain.repository

import com.ssafy.domain.model.recommend.RecommendDomainModel
import com.ssafy.domain.utils.ViewState

interface RecommendRepository {
    suspend fun getRecommendList(): ViewState<List<RecommendDomainModel>>
}