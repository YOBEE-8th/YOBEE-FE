package com.ssafy.yobee.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.model.account.AccountnfoDomainModel
import com.ssafy.domain.model.account.KakaoAccountDomainModel
import com.ssafy.domain.model.account.UserInfoDomainModel
import com.ssafy.domain.usecase.account.AutoLoginUseCase
import com.ssafy.domain.usecase.account.GetKakaoAccountInfoUseCase
import com.ssafy.domain.usecase.account.LoginUseCase
import com.ssafy.domain.usecase.account.SocialLoginUseCase
import com.ssafy.domain.usecase.fcm.GetFcmTokenUseCase
import com.ssafy.domain.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val autoLoginUseCase: AutoLoginUseCase,
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val socialLoginUseCase: SocialLoginUseCase,
    private val getKakaoAccountInfoUseCase: GetKakaoAccountInfoUseCase,
) :
    ViewModel() {

    private var fcmToken: String = ""

    private var _kakaoAccountInfo = KakaoAccountDomainModel(0, "", "", null)
    val kakaoAccountInfo: KakaoAccountDomainModel get() = _kakaoAccountInfo

    private val _loginLiveData = SingleLiveEvent<ViewState<UserInfoDomainModel>>()
    val loginLiveData: LiveData<ViewState<UserInfoDomainModel>> get() = _loginLiveData

    private val _autoLoginLiveData = SingleLiveEvent<ViewState<AccountnfoDomainModel>>()
    val autoLoginLiveData: LiveData<ViewState<AccountnfoDomainModel>> get() = _autoLoginLiveData

    private val _socialLoginLiveData = SingleLiveEvent<ViewState<AccountnfoDomainModel>>()
    val socialLoginLiveData: LiveData<ViewState<AccountnfoDomainModel>> get() = _socialLoginLiveData

    fun getFcmToken() {
        fcmToken = getFcmTokenUseCase.invoke()
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginLiveData.postValue(ViewState.Loading())
        when (val result = loginUseCase(email, password, fcmToken)) {
            is ViewState.Success -> {
                _loginLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _loginLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }

    fun autoLogin(email: String, password: String) = viewModelScope.launch {
        _autoLoginLiveData.postValue(ViewState.Loading())
        when (val result = autoLoginUseCase(email, password, fcmToken)) {
            is ViewState.Success -> {
                _autoLoginLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _autoLoginLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }

    fun socialLogin(email: String, type: Int) = viewModelScope.launch {
        _socialLoginLiveData.postValue(ViewState.Loading())
        fcmToken = getFcmTokenUseCase()
        when (val result = socialLoginUseCase(email, type, fcmToken)) {
            is ViewState.Success -> {
                _socialLoginLiveData.postValue(result)
            }
            is ViewState.Error -> {
                if (result.message == "700") {
                    val viewState = ViewState.Error<AccountnfoDomainModel>(type.toString(), null)
                    _socialLoginLiveData.postValue(viewState)
                } else {
                    _socialLoginLiveData.postValue(result)
                }
            }
            is ViewState.Loading -> {

            }
        }
    }

    fun getKakaoAccountInfo(accessToken: String) = viewModelScope.launch {
        getKakaoAccountInfoUseCase(accessToken).run {
            when (this) {
                is ViewState.Success -> {
                    Log.d("TAG", "getKakaoAccountInfo: ${value!!.id}")
                    _kakaoAccountInfo = value!!
                    if (value!!.email == "") {
                        socialLogin(value!!.id.toString(), 2)
                    } else {
                        socialLogin(value!!.email!!, 2)
                    }
                }
                is ViewState.Error -> {
                    Log.d("TAG", "getKakaoAccountInfo: 카카오 회원정보 조회 실패")
                }
                is ViewState.Loading -> {
                }
            }
        }
    }
}