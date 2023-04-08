package com.ssafy.yobee.ui.mypage

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.usecase.account.ChangePasswordUseCase
import com.ssafy.domain.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

private const val TAG = "ChangePasswordViewModel_요비"

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase,
) : ViewModel() {

    private var _password: String = ""
    val password: String
        get() = _password

    private var _passwordState = SingleLiveEvent<Int>()
    val passwordState: LiveData<Int>
        get() = _passwordState

    fun passwordWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                _password = s.toString()
                if (s.isEmpty()) {
                    _passwordState.postValue(0)
                } else {
                    if (!Pattern.matches(
                            "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", s
                        )
                    ) _passwordState.postValue(1)
                    else _passwordState.postValue(2)
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        }
    }

    private var _passwordConfirmState = SingleLiveEvent<Boolean>()
    val passwordConfirmState: LiveData<Boolean>
        get() = _passwordConfirmState

    fun checkConfirm(pwConfirm: String) {
        if (password.isNotEmpty()) {
            if (pwConfirm == password) {
                _passwordConfirmState.postValue(true)
            } else {
                _passwordConfirmState.postValue(false)
            }
        } else {
            _passwordConfirmState.postValue(false)
        }
    }

    private var _checkedPassword: Boolean = false
    val checkedPassword: Boolean
        get() = _checkedPassword

    fun setCheckPassword(state: Boolean) {
        _checkedPassword = state
    }

    private val _changePasswordLiveData = SingleLiveEvent<ViewState<Void>>()
    val changePasswordLiveData: LiveData<ViewState<Void>> get() = _changePasswordLiveData

    fun changePassword() = viewModelScope.launch {
        _changePasswordLiveData.postValue(ViewState.Loading())
        when (val result = changePasswordUseCase(password)) {
            is ViewState.Success -> {
                _changePasswordLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _changePasswordLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }

}
