package com.ssafy.yobee.ui.mypage_like_recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.model.recipe.RecipeDomainModel
import com.ssafy.domain.usecase.recipe.ChangeRecipeLikeStatusUseCase
import com.ssafy.domain.usecase.user.GetLikeRecipeListUseCase
import com.ssafy.domain.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikeRecipeViewModel @Inject constructor(
    private val getLikeRecipeListUseCase: GetLikeRecipeListUseCase,
    private val changeRecipeLikeStatusUseCase: ChangeRecipeLikeStatusUseCase,
) : ViewModel() {

    private val _recipeList = SingleLiveEvent<ViewState<List<RecipeDomainModel>>>()
    val recipeList: LiveData<ViewState<List<RecipeDomainModel>>> get() = _recipeList

    fun getLikeRecipeList() = viewModelScope.launch {
        _recipeList.postValue(ViewState.Loading())
        getLikeRecipeListUseCase().run {
            when (this) {
                is ViewState.Success -> {
                    _recipeList.postValue(this)
                }
                is ViewState.Error -> {
                    _recipeList.postValue(this)
                }
                is ViewState.Loading -> {
                }
            }
        }
    }

    fun changeRecipeLikeStatus(recipeId: Int) = viewModelScope.launch {
        changeRecipeLikeStatusUseCase(recipeId).run {
            when (this) {
                is ViewState.Error -> {
                    Log.d("TAG", "addLikeRecipe: $message")
                }
                is ViewState.Loading -> {
                    Log.d("TAG", "addLikeRecipe: $message")
                }
                is ViewState.Success -> {
                    getLikeRecipeList()
                }
            }
        }
    }
}