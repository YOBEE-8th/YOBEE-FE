package com.ssafy.yobee.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.usecase.user.ReviewByRecipeUseCase
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.ui.mypage.model.ReviewByRecipeModel
import com.ssafy.yobee.ui.mypage.model.toReviewByRecipeModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewByRecipeViewModel @Inject constructor(
    private val reviewByRecipeUseCase: ReviewByRecipeUseCase,
) : ViewModel() {
    private val _reviewByRecipeLiveData = SingleLiveEvent<ViewState<List<ReviewByRecipeModel>>>()
    val reviewByRecipeLiveData: LiveData<ViewState<List<ReviewByRecipeModel>>>
        get() = _reviewByRecipeLiveData

    fun getReviewByRecipe() = viewModelScope.launch {
        _reviewByRecipeLiveData.postValue(ViewState.Loading())

        when (val result = reviewByRecipeUseCase()) {
            is ViewState.Success -> {
                val reviewByRecipeModelList = arrayListOf<ReviewByRecipeModel>()
                result.value!!.forEach { reviewByRecipeDomainModel ->
                    reviewByRecipeModelList.add(reviewByRecipeDomainModel.toReviewByRecipeModel())
                }

                _reviewByRecipeLiveData.postValue(
                    ViewState.Success(result.message, reviewByRecipeModelList)
                )
            }
            is ViewState.Error -> {
                _reviewByRecipeLiveData.postValue(ViewState.Error(result.message))
            }
            is ViewState.Loading -> {}
        }
    }
}