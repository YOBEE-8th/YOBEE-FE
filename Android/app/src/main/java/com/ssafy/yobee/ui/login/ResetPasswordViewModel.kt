package com.ssafy.yobee.ui.login

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.usecase.account.ResetPasswordUseCase
import com.ssafy.domain.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
) : ViewModel() {

    private val emailValidation =
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    private var _email: String = ""
    val email: String
        get() = _email

    private var _emailState = SingleLiveEvent<Int>()
    val emailState: LiveData<Int>
        get() = _emailState

    fun emailWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                _email = s.toString()
                if (s.isEmpty()) {
                    _emailState.postValue(0)
                } else {
                    if (Pattern.matches(emailValidation, email)) _emailState.postValue(1)
                    else _emailState.postValue(2)
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        }
    }

    private val _resetPasswordLiveData = SingleLiveEvent<ViewState<Void>>()
    val resetPasswordLiveData: LiveData<ViewState<Void>>
        get() = _resetPasswordLiveData

    fun resetPassword() = viewModelScope.launch {
        _resetPasswordLiveData.postValue(ViewState.Loading())
        when (val result = resetPasswordUseCase(email)) {
            is ViewState.Success -> {
                _resetPasswordLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _resetPasswordLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }
}