package com.ssafy.yobee.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.model.recipe.RecipeDomainModel
import com.ssafy.domain.usecase.recipe.ChangeRecipeLikeStatusUseCase
import com.ssafy.domain.usecase.recipe.GetSearchedRecipeListUseCase
import com.ssafy.domain.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeSearchViewModel @Inject constructor(
    private val getSearchedRecipeListUseCase: GetSearchedRecipeListUseCase,
    private val changeRecipeLikeStatusUseCase: ChangeRecipeLikeStatusUseCase,
) :
    ViewModel() {
    private val _keyword = SingleLiveEvent<String>()
    val keyword: LiveData<String> get() = _keyword

    private val _order = SingleLiveEvent<Boolean>()
    val order: LiveData<Boolean> get() = _order

    private val _sortBy = SingleLiveEvent<Int>()
    val sortBy: LiveData<Int> get() = _sortBy

    private val _isAI = SingleLiveEvent<Boolean>()
    val isAI: LiveData<Boolean> get() = _isAI

    private val _searchedRecipeList = SingleLiveEvent<ViewState<List<RecipeDomainModel>>>()
    val searchedRecipeList: LiveData<ViewState<List<RecipeDomainModel>>> get() = _searchedRecipeList

    fun setSortBy(key: Int) {
        _sortBy.postValue(key)
    }

    fun setIsAI() {
        _isAI.postValue(!(_isAI.value ?: false))
    }

    fun setOrder() {
        _order.postValue(!(_order.value ?: false))
    }

    fun getSearchedRecipeList() = viewModelScope.launch {
        _searchedRecipeList.postValue(ViewState.Loading())
        when (val result = getSearchedRecipeListUseCase(
            keyword.value ?: "",
            sortBy.value ?: 0,
            order.value ?: false,
            isAI.value ?: false
        )) {
            is ViewState.Success -> {
                _searchedRecipeList.postValue(result)
            }
            is ViewState.Error -> {
                _searchedRecipeList.postValue(result)
            }
            is ViewState.Loading -> {}
        }
    }

    fun changeRecipeLikeStatus(recipeId: Int) = viewModelScope.launch {
        when (val response = changeRecipeLikeStatusUseCase.invoke(recipeId)) {
            is ViewState.Error -> {
                ViewState.Error(response.message, null)
            }
            is ViewState.Loading -> {}
            is ViewState.Success -> {
                getSearchedRecipeList()
            }
        }
    }

    fun setKeyword(keyword: String) {
        _keyword.postValue(keyword)
    }
}