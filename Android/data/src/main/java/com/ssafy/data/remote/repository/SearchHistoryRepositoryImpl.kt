package com.ssafy.data.remote.repository

import com.ssafy.data.local.prefs.SearchKeywordPreference
import com.ssafy.domain.repository.SearchHistoryRepository
import com.ssafy.domain.utils.ViewState
import javax.inject.Inject

internal class SearchHistoryRepositoryImpl @Inject constructor(private val searchKeywordPreference: SearchKeywordPreference) :
    SearchHistoryRepository {
    override suspend fun getHistoryKeyword(): ViewState<List<String>> {
        val response = searchKeywordPreference.getKeywordList()
        val subListSize = if (response.size > 5) 5
        else response.size
        return ViewState.Success("검색 기록 불러오기 성공", response.subList(0, subListSize))

    }

    override suspend fun deleteHistoryKeyword(baseList: List<String>, index: Int): ViewState<Int> {
        val baseSize = baseList.size
        searchKeywordPreference.removeKeyword(baseList, index)
        return if (getHistoryKeyword().value!!.size != baseSize) ViewState.Success(
            "삭제 성공",
            null
        ) else ViewState.Error("삭제 실패", null)
    }

    override suspend fun clearHistoryKeyword(): ViewState<Int> {
        searchKeywordPreference.removeAllKeyword()
        return if (getHistoryKeyword().value!!.isEmpty()) ViewState.Success(
            "삭제 성공",
            null
        ) else ViewState.Error("삭제 실패", null)
    }

    override suspend fun addHistoryKeyword(keyword: String): ViewState<Int> {
        searchKeywordPreference.addKeyword(keyword)
        val checkList = getHistoryKeyword().value ?: emptyList()
        return if (checkList[0] == keyword) ViewState.Success("검색기록 저장", null) else ViewState.Error(
            "검색기록 저장 실패",
            null
        )
    }

}