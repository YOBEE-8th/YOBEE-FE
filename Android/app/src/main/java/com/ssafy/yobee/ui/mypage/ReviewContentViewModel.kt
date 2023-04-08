package com.ssafy.yobee.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.usecase.review.DeleteReviewUseCase
import com.ssafy.domain.usecase.review.GetReviewUseCase
import com.ssafy.domain.usecase.review.UpdateReviewUseCase
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.ui.cook.model.GetReviewModel
import com.ssafy.yobee.ui.cook.model.toGetReviewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewContentViewModel @Inject constructor(
    private val getReviewUseCase: GetReviewUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
    private val updateReviewUseCase: UpdateReviewUseCase,
) : ViewModel() {
    private val _getReviewLiveData = SingleLiveEvent<ViewState<GetReviewModel>>()
    val getReviewLiveData: LiveData<ViewState<GetReviewModel>>
        get() = _getReviewLiveData

    fun getReview(reviewId: Long) = viewModelScope.launch {
        _getReviewLiveData.postValue(ViewState.Loading())

        when (val result = getReviewUseCase(reviewId)) {
            is ViewState.Success -> {
                _getReviewLiveData.postValue(
                    ViewState.Success(
                        result.message,
                        result.value!!.toGetReviewModel()
                    )
                )
            }
            is ViewState.Error -> {
                _getReviewLiveData.postValue(ViewState.Error(result.message))
            }
            is ViewState.Loading -> {}
        }
    }

    private val _deleteReviewLiveData = SingleLiveEvent<ViewState<Any>>()
    val deleteReviewLiveData: LiveData<ViewState<Any>>
        get() = _deleteReviewLiveData

    fun deleteReview(reviewId: Int) = viewModelScope.launch {
        when (val result = deleteReviewUseCase(reviewId)) {
            is ViewState.Success -> {
                _deleteReviewLiveData.postValue(ViewState.Success(result.message, result.value))
            }
            is ViewState.Error -> {
                _deleteReviewLiveData.postValue(ViewState.Error(result.message))
            }
            is ViewState.Loading -> {}
        }
    }

    private val _updateReviewLiveData = SingleLiveEvent<ViewState<Any>>()
    val updateReviewLiveData: LiveData<ViewState<Any>>
        get() = _updateReviewLiveData

    fun updateReview(reviewId: Int, content: String) = viewModelScope.launch {
        _updateReviewLiveData.postValue(ViewState.Loading())

        when (val result = updateReviewUseCase(reviewId, content)) {
            is ViewState.Success -> {
                _updateReviewLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _updateReviewLiveData.postValue(result)
            }
            is ViewState.Loading -> {}
        }
    }
}