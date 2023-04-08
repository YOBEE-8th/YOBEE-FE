package com.ssafy.domain.repository

import com.ssafy.domain.utils.ViewState

interface SearchHistoryRepository {
    suspend fun getHistoryKeyword(): ViewState<List<String>>
    suspend fun deleteHistoryKeyword(baseList: List<String>, index: Int): ViewState<Int>
    suspend fun clearHistoryKeyword(): ViewState<Int>
    suspend fun addHistoryKeyword(keyword: String): ViewState<Int>
}