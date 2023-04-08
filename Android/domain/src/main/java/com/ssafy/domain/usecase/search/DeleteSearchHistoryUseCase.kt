package com.ssafy.domain.usecase.search

import com.ssafy.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class DeleteSearchHistoryUseCase @Inject constructor(private val searchHistoryRepository: SearchHistoryRepository) {
    suspend operator fun invoke(keywordList: List<String>, index: Int) =
        searchHistoryRepository.deleteHistoryKeyword(keywordList, index)
}