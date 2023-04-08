package com.ssafy.yobee.ui.recipe_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.model.recipe.RecipeDomainModel
import com.ssafy.domain.usecase.recipe.ChangeRecipeLikeStatusUseCase
import com.ssafy.domain.usecase.recipe.GetRecipeListUseCase
import com.ssafy.domain.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val getRecipeListUseCase: GetRecipeListUseCase,
    private val changeRecipeLikeStatusUseCase: ChangeRecipeLikeStatusUseCase,
) :
    ViewModel() {
    private var page = 0

    private var _dataList = mutableListOf<RecipeDomainModel>()
    val dataList: List<RecipeDomainModel> get() = _dataList

    private val _category = SingleLiveEvent<String>()
    val category: LiveData<String> get() = _category

    private val _order = MutableLiveData<Boolean>()
    val order: LiveData<Boolean> get() = _order

    private val _sortBy = MutableLiveData<Int>()
    val sortBy: LiveData<Int> get() = _sortBy

    private val _isAI = MutableLiveData<Boolean>()
    val isAI: LiveData<Boolean> get() = _isAI

    fun plusPage() {
        page++
    }

    fun minusPage() {
        page--
    }

    fun initPage() {
        page = 0
    }

    fun addDataList(data: List<RecipeDomainModel>) {
        _dataList.addAll(data)
    }

    fun clearDataList() {
        _dataList.clear()
    }

    fun setSortBy(key: Int) {
        _sortBy.value = key
    }

    fun setIsAI() {
        _isAI.postValue(!(_isAI.value ?: false))
    }

    fun setOrder() {
        _order.value = !(_order.value ?: false)
    }

    private val _recipeList = SingleLiveEvent<ViewState<List<RecipeDomainModel>>>()
    val recipeList: LiveData<ViewState<List<RecipeDomainModel>>> get() = _recipeList

    fun getRecipeList(page: Int = this.page, refresh: Boolean = false) = viewModelScope.launch {
        _recipeList.postValue(ViewState.Loading())
        when (val response = getRecipeListUseCase(
            category.value ?: "all",
            sortBy.value ?: 0,
            order.value ?: false,
            isAI.value ?: false,
            page
        )) {
            is ViewState.Success -> {
                if (refresh) {
                    _recipeList.postValue(ViewState.Success("refresh", response.value))
                } else {
                    if (response.value!!.isEmpty()) {
                        minusPage()
                    }
                    _recipeList.postValue(response)

                }

            }
            is ViewState.Error -> {
                _recipeList.postValue(response)
            }
            is ViewState.Loading -> {
            }
        }
    }

    fun changeRecipeLikeStatus(recipeId: Int) = viewModelScope.launch {
        when (val response = changeRecipeLikeStatusUseCase(recipeId)) {
            is ViewState.Error -> {
                Log.d("TAG", "addLikeRecipe: ${response.message}")
            }
            is ViewState.Loading -> {
                Log.d("TAG", "addLikeRecipe: ${response.message}")
            }
            is ViewState.Success -> {
                var changed = -1
                for (i in 0 until _dataList.size){
                    if (_dataList[i].recipeId == recipeId){
                        changed = i
                        if (_dataList[i].isLike){
                            _dataList[i] = RecipeDomainModel(
                                _dataList[i].recipeId,
                                _dataList[i].imageUrl,
                                _dataList[i].title,
                                _dataList[i].likeCount - 1,
                                _dataList[i].isAI,
                                !_dataList[i].isLike,
                                _dataList[i].difficulty
                            )
                        } else {
                            _dataList[i] = RecipeDomainModel(
                                _dataList[i].recipeId,
                                _dataList[i].imageUrl,
                                _dataList[i].title,
                                _dataList[i].likeCount + 1,
                                _dataList[i].isAI,
                                !_dataList[i].isLike,
                                _dataList[i].difficulty
                            )
                        }
                        break
                    }
                }
                _recipeList.postValue(ViewState.Success("refresh $changed", null))
            }
        }

    }

    fun setCategory(category: String) {
        if (_category.value == null) {
            if (category == "밥/반찬") {
                _category.postValue("반찬")
            } else {
                _category.postValue(category)
            }
        }
    }
}