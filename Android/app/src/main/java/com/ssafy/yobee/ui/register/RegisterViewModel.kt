package com.ssafy.yobee.ui.register

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.model.account.AccountnfoDomainModel
import com.ssafy.domain.usecase.account.CheckVerificationCodeUseCase
import com.ssafy.domain.usecase.account.JoinAccountUseCase
import com.ssafy.domain.usecase.account.ValidateEmailUseCase
import com.ssafy.domain.usecase.account.ValidateNickNameUseCase
import com.ssafy.domain.usecase.fcm.GetFcmTokenUseCase
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.ui.register.model.RegisterUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.regex.Pattern
import javax.inject.Inject

private const val TAG = "RegisterViewModel_요비"

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val checkVerificationCodeUseCase: CheckVerificationCodeUseCase,
    private val validateNickNameUseCase: ValidateNickNameUseCase,
    private val joinAccountUseCase: JoinAccountUseCase,
) :
    ViewModel() {

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
                _email = s.toString().trim()
                _emailState.postValue(-1)
            }

            override fun afterTextChanged(s: Editable) {}
        }
    }

    fun checkEmailRegularExpression() {
        if (email.isEmpty()) _emailState.postValue(0)
        else {
            if (Pattern.matches(emailValidation, email)) {
                _emailState.postValue(1)
                validateEmail()
            } else _emailState.postValue(2)
        }
    }

    private val _validateEmailLiveData = SingleLiveEvent<ViewState<Void>>()
    val validateEmailLiveData: LiveData<ViewState<Void>> get() = _validateEmailLiveData

    private fun validateEmail() = viewModelScope.launch {
        _validateEmailLiveData.postValue(ViewState.Loading())
        when (val result = validateEmailUseCase(email)) {
            is ViewState.Success -> {
                _validateEmailLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _validateEmailLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }

    private var _verificationCode: String = ""
    val verificationCode: String
        get() = _verificationCode

    private var _verificationCodeState = SingleLiveEvent<Boolean>()
    val verificationCodeState: LiveData<Boolean>
        get() = _verificationCodeState

    fun setVerificationCodeState(state: Boolean) = _verificationCodeState.postValue(state)

    fun checkVerificationCodeState(state: String, code: String) {
        if (state == "확인") checkVerificationCode(code)
        else {
            validateEmail()
            _verificationCodeState.postValue(false)
        }
    }

    private val _checkVerificationCodeLiveData = SingleLiveEvent<ViewState<Void>>()
    val checkVerificationCodeLiveData: LiveData<ViewState<Void>> get() = _checkVerificationCodeLiveData

    private fun checkVerificationCode(code: String) = viewModelScope.launch {
        _checkVerificationCodeLiveData.postValue(ViewState.Loading())
        when (val result = checkVerificationCodeUseCase(code)) {
            is ViewState.Success -> {
                _checkVerificationCodeLiveData.postValue(result)
                _verificationCode = code
            }
            is ViewState.Error -> {
                _checkVerificationCodeLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }

    private var _emailCheck: Int = 0
    val emailCheck: Int
        get() = _emailCheck

    fun checkedEmail(state: Int) {
        _emailCheck = state
    }

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

    private var _passwordConfirmState = SingleLiveEvent<Int>()
    val passwordConfirmState: LiveData<Int>
        get() = _passwordConfirmState

    fun checkConfirm(pwConfirm: String) {
        if (password.isNotEmpty()) {
            if (pwConfirm == password) {
                _passwordConfirmState.postValue(1)
            } else {
                _passwordConfirmState.postValue(0)
            }
        } else {
            _passwordConfirmState.postValue(2)
        }
    }

    private var _passwordCheck: Int = 0
    val passwordCheck: Int
        get() = _passwordCheck

    fun checkedPassword(state: Int) {
        _passwordCheck = state
    }

    private var _profileImageUrl: String? = ""
    val profileImageUrl: String?
        get() = _profileImageUrl


    private val _profileImage = SingleLiveEvent<Uri?>()
    val profileImage: LiveData<Uri?>
        get() = _profileImage

    fun initProfileImage() {
        if (profileImageUrl == "") _profileImage.setValue(Uri.parse("https://yobee.s3.ap-northeast-2.amazonaws.com/default_profile.png"))
        else _profileImage.setValue(Uri.parse(profileImageUrl))
    }

    private val _profileImageMultipartBodyLiveData = SingleLiveEvent<MultipartBody.Part?>()
    val profileImageMultipartBodyLiveData: LiveData<MultipartBody.Part?>
        get() = _profileImageMultipartBodyLiveData

    fun removeImageUrl() {
        _profileImageUrl = ""
    }

    fun setUserImageUri(imageUri: Uri) {
        _profileImage.setValue(imageUri)
    }

    fun setUserImageUriToMultipart(imageUri: MultipartBody.Part) {
        _profileImageMultipartBodyLiveData.postValue(imageUri)
    }

    private var _nickname: String = ""
    val nickname: String get() = _nickname

    fun nicknameCheck(nickname: String) {
        if (nickname.isNotEmpty()) {
            validateNickname(nickname)
            _nicknameState.postValue(true)
        } else _nicknameState.postValue(false)
    }

    private var _nicknameState = SingleLiveEvent<Boolean>()
    val nicknameState: LiveData<Boolean>
        get() = _nicknameState

    fun setNicknameState(state: Boolean) {
        _nicknameState.postValue(state)
    }

    private val _validateNickname = SingleLiveEvent<ViewState<Void>>()
    val validateNickname: LiveData<ViewState<Void>> get() = _validateNickname

    private fun validateNickname(nickname: String) = viewModelScope.launch {
        _validateNickname.postValue(ViewState.Loading())
        when (val result = validateNickNameUseCase(nickname)) {
            is ViewState.Success -> {
                _validateNickname.postValue(result)
                _nickname = nickname
            }
            is ViewState.Error -> {
                _validateNickname.postValue(result)
                _nickname = ""
            }
            is ViewState.Loading -> {

            }
        }
    }

    private var _nicknameCheck: Int = 0
    val nicknameCheck: Int
        get() = _nicknameCheck


    fun checkedNickName(state: Int) {
        _nicknameCheck = state
    }

    private var fcmToken = ""

    private var _type: Int = 0
    val type: Int
        get() = _type

    private val _joinAccountLiveData = SingleLiveEvent<ViewState<AccountnfoDomainModel>>()
    val joinAccountLiveData: LiveData<ViewState<AccountnfoDomainModel>> get() = _joinAccountLiveData


    fun setAccountInfo(account: RegisterUIModel) {
        _email = account.email
        _password = account.password
        _nickname = account.nickname
        _profileImageMultipartBodyLiveData.postValue(null)
        _profileImageUrl = account.profileImgUrl
        _verificationCode = account.verificationCode
        _type = account.type
        _emailCheck = account.emailCheck
        _passwordCheck = account.passwordCheck
        _nicknameCheck = account.nicknameCheck
        Log.d(
            TAG,
            "세팅된 값: $email $password $nickname ${profileImageMultipartBodyLiveData.value} $profileImageUrl $type $emailCheck $passwordCheck $nicknameCheck"
        )
    }


    fun clearJoinData() {
        _email = ""
        _password = ""
        _nickname = ""
        _profileImageMultipartBodyLiveData.postValue(null)
        _profileImageUrl = ""
        fcmToken = ""
        _verificationCode = ""
        _type = 0
        _emailCheck = 0
        _passwordCheck = 0
        _nicknameCheck = 0
    }

    fun joinAccount() {

        fcmToken = getFcmTokenUseCase.invoke()

        var requestHashMap: HashMap<String, RequestBody> = HashMap()

        requestHashMap["email"] =
            email.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        requestHashMap["password"] =
            password.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        requestHashMap["nickname"] =
            nickname.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        requestHashMap["profileImageUrl"] =
            profileImageUrl!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        requestHashMap["fcmToken"] =
            fcmToken.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        requestHashMap["type"] =
            type.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        viewModelScope.launch {
            _joinAccountLiveData.postValue(ViewState.Loading())
            when (val result =
                joinAccountUseCase(profileImageMultipartBodyLiveData.value, requestHashMap)) {
                is ViewState.Success -> {
                    _joinAccountLiveData.postValue(result)
                }
                is ViewState.Error -> {
                    _joinAccountLiveData.postValue(result)
                }
                is ViewState.Loading -> {

                }
            }
        }
    }
}

