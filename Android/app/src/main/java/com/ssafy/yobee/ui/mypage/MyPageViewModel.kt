package com.ssafy.yobee.ui.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.model.account.LogoutDomainModel
import com.ssafy.domain.model.account.WithdrawalDomainModel
import com.ssafy.domain.usecase.account.*
import com.ssafy.domain.usecase.user.ExpUseCase
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.ui.mypage.model.ExpModel
import com.ssafy.yobee.ui.mypage.model.toExpModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val expUseCase: ExpUseCase,
    private val getNicknameUseCase: GetNicknameUseCase,
    private val getProfileImageUseCase: GetProfileImageUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val withdrawalUseCase: WithdrawalUseCase,
    private val getLoginTypeUseCase: GetLoginTypeUseCase,
) : ViewModel() {
    private val _expLiveData = SingleLiveEvent<ViewState<ExpModel>>()
    val expLiveData: LiveData<ViewState<ExpModel>>
        get() = _expLiveData

    fun getExp() = viewModelScope.launch {
        _expLiveData.postValue(ViewState.Loading())
        when (val result = expUseCase()) {
            is ViewState.Success -> {
                _expLiveData.postValue(
                    ViewState.Success(
                        result.message,
                        result.value!!.toExpModel()
                    )
                )
            }
            is ViewState.Error -> {
                _expLiveData.postValue(ViewState.Error(result.message))
            }
            is ViewState.Loading -> {
            }
        }
    }

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

    private val _nickname = SingleLiveEvent<ViewState<String>>()
    val nickname: LiveData<ViewState<String>> get() = _nickname

    fun getNickname() = viewModelScope.launch {
        when (val response = getNicknameUseCase()) {
            is ViewState.Success -> {
                _nickname.postValue(response)
            }
            is ViewState.Error -> {
                _nickname.postValue(response)
            }
            is ViewState.Loading -> {}
        }
    }

    private val _logoutLiveData = SingleLiveEvent<ViewState<LogoutDomainModel>>()
    val logoutLiveData: LiveData<ViewState<LogoutDomainModel>> get() = _logoutLiveData

    fun logout() = viewModelScope.launch {
        _logoutLiveData.postValue(ViewState.Loading())
        when (val result = logoutUseCase()) {
            is ViewState.Success -> {
                _logoutLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _logoutLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }

    private val _withdrawalLiveData = SingleLiveEvent<ViewState<WithdrawalDomainModel>>()
    val withdrawalLiveData: LiveData<ViewState<WithdrawalDomainModel>> get() = _withdrawalLiveData

    fun withdrawal() = viewModelScope.launch {
        _withdrawalLiveData.postValue(ViewState.Loading())

        when (val result = withdrawalUseCase()) {
            is ViewState.Success -> {
                _withdrawalLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _withdrawalLiveData.postValue(result)
            }
            is ViewState.Loading -> {}
        }
    }

    private var _loginType: Int = 0
    val loginType: Int
        get() = _loginType

    init {
        _loginType = getLoginTypeUseCase()
    }
}
