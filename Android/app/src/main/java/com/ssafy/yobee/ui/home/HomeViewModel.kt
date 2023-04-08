package com.ssafy.yobee.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.usecase.account.GetProfileImageUseCase
import com.ssafy.domain.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProfileImageUseCase: GetProfileImageUseCase,
) : ViewModel() {
    private val _profileImage = SingleLiveEvent<ViewState<String>>()
    val profileImage: LiveData<ViewState<String>> get() = _profileImage

    fun getProfileImage() = viewModelScope.launch {
        when (val response = getProfileImageUseCase()) {
            is ViewState.Success -> {
                _profileImage.postValue(response)
            }
            is ViewState.Error -> {
                _profileImage.postValue(response)
            }
            is ViewState.Loading -> {}
        }
    }
}