package com.ssafy.yobee.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.model.auth.AuthDomainModel
import com.ssafy.domain.usecase.account.AutoLoginCheckUseCase
import com.ssafy.domain.usecase.auth.GetAccessTokenUseCase
import com.ssafy.domain.usecase.auth.GetRefreshTokenUseCase
import com.ssafy.domain.usecase.auth.ValidateTokenUseCase
import com.ssafy.domain.usecase.fcm.AddFcmTokenUseCase
import com.ssafy.domain.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val addFcmTokenUseCase: AddFcmTokenUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getRefreshTokenUseCase: GetRefreshTokenUseCase,
    private val validateTokenUseCase: ValidateTokenUseCase,
    private val autoLoginCheckUseCase: AutoLoginCheckUseCase,
) :
    ViewModel() {

    private var accessToken: String? = ""
    private var refreshToken: String? = ""
    var autoLoginCheckValue: String? = ""

    private val _validateTokenLiveData = SingleLiveEvent<ViewState<AuthDomainModel>>()
    val validateTokenLiveData: LiveData<ViewState<AuthDomainModel>> get() = _validateTokenLiveData

    fun getAccessToken(): String? {
        accessToken = getAccessTokenUseCase.invoke()
        return accessToken
    }

    fun getRefreshToken(): String? {
        refreshToken = getRefreshTokenUseCase.invoke()
        return refreshToken
    }

    fun addFcmToken(token: String) {
        addFcmTokenUseCase.invoke(token)
    }

    fun autoLoginCheck() {
        autoLoginCheckValue = autoLoginCheckUseCase.invoke()
    }

    fun validateToken() = viewModelScope.launch {
        _validateTokenLiveData.postValue(ViewState.Loading())
        when (val result = validateTokenUseCase(accessToken!!, refreshToken!!)) {
            is ViewState.Success -> {
                _validateTokenLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _validateTokenLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }

}