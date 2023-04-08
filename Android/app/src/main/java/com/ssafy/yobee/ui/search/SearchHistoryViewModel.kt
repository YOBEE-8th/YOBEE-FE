package com.ssafy.yobee.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.usecase.search.AddSearchHistroyUseCase
import com.ssafy.domain.usecase.search.ClearSearchHistoryUseCase
import com.ssafy.domain.usecase.search.DeleteSearchHistoryUseCase
import com.ssafy.domain.usecase.search.GetSearchHistoryUseCase
import com.ssafy.domain.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchHistoryViewModel @Inject constructor(
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val deleteSearchHistoryUseCase: DeleteSearchHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase,
    private val addSearchHistroyUseCase: AddSearchHistroyUseCase,
) : ViewModel() {
    private val _searchHistoryList = SingleLiveEvent<ViewState<List<String>>>()
    val searchHistoryList: LiveData<ViewState<List<String>>> get() = _searchHistoryList

    fun addSearchHistory(keyword: String) = viewModelScope.launch {
        if (keyword != "") {
            when (val response = addSearchHistroyUseCase(keyword)) {
                is ViewState.Error -> {
                    Log.d("TAG", "addSearchHistory: ${response.message}")
                }
                is ViewState.Loading -> {
                }
                is ViewState.Success -> {
                    getSearchHistory()
                    Log.d("TAG", "addSearchHistory: ${response.message}")
                }
            }
        }
    }

    fun deleteSearchHistory(baseList: List<String>, index: Int) = viewModelScope.launch {
        when (val response = deleteSearchHistoryUseCase(baseList, index)) {
            is ViewState.Error -> {
                Log.d("TAG", "deleteSearchHistory: ${response.message}")
            }
            is ViewState.Loading -> {}
            is ViewState.Success -> {
                getSearchHistory()
                Log.d("TAG", "deleteSearchHistory: ${response.message}")
            }
        }
    }

    fun clearSearchHistory() = viewModelScope.launch {
        when (val response = clearSearchHistoryUseCase()) {
            is ViewState.Error -> {
                Log.d("TAG", "clearSearchHistory: ${response.message}")
            }
            is ViewState.Loading -> {}
            is ViewState.Success -> {
                Log.d("TAG", "clearSearchHistory: ${response.message}")
            }
        }
    }

    fun getSearchHistory() = viewModelScope.launch {
        when (val response = getSearchHistoryUseCase()) {
            is ViewState.Error -> {
                _searchHistoryList.postValue(ViewState.Error(response.message, response.value))
            }
            is ViewState.Loading -> {}
            is ViewState.Success -> {
                _searchHistoryList.postValue(ViewState.Success(response.message, response.value))
            }
        }
    }
}