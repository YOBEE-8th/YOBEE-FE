package com.ssafy.yobee.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.usecase.user.ReviewByDateUseCase
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.ui.mypage.model.ReviewByDateModel
import com.ssafy.yobee.ui.mypage.model.toReviewByDateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewByDateViewModel @Inject constructor(
    private val reviewByDateUseCase: ReviewByDateUseCase,
) : ViewModel() {
    private val _reviewByDateLiveData = SingleLiveEvent<ViewState<List<ReviewByDateModel>>>()
    val reviewByDateLiveData: LiveData<ViewState<List<ReviewByDateModel>>>
        get() = _reviewByDateLiveData

    fun getReviewByDate(recipeId: Long) = viewModelScope.launch {
        _reviewByDateLiveData.postValue(ViewState.Loading())

        when (val result = reviewByDateUseCase(recipeId)) {
            is ViewState.Success -> {
                val reviewByDateModelList = arrayListOf<ReviewByDateModel>()
                result.value!!.forEach { reviewByDateDomainModel ->
                    reviewByDateModelList.add(reviewByDateDomainModel.toReviewByDateModel())
                }

                _reviewByDateLiveData.postValue(
                    ViewState.Success(result.message, reviewByDateModelList)
                )
            }
            is ViewState.Error -> {
                _reviewByDateLiveData.postValue(ViewState.Error(result.message))
            }
            is ViewState.Loading -> {}
        }
    }
}