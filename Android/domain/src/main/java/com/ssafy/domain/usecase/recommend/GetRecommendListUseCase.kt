package com.ssafy.domain.usecase.recommend

import com.ssafy.domain.repository.RecommendRepository
import javax.inject.Inject

class GetRecommendListUseCase @Inject constructor(private val recommendRepository: RecommendRepository) {
    suspend operator fun invoke() = recommendRepository.getRecommendList()
}