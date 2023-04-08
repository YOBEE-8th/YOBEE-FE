package com.ssafy.domain.usecase.search

import com.ssafy.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class ClearSearchHistoryUseCase @Inject constructor(private val searchHistoryRepository: SearchHistoryRepository) {
    suspend operator fun invoke() = searchHistoryRepository.clearHistoryKeyword()
}