package com.ssafy.yobee.ui.mypage

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.model.account.ProfileDomainModel
import com.ssafy.domain.usecase.account.UpdateProfileUseCase
import com.ssafy.domain.usecase.account.ValidateNickNameAuthUseCase
import com.ssafy.domain.usecase.account.getProfileUseCase
import com.ssafy.domain.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

private const val TAG = "MyPageProfileViewModel_요비"

@HiltViewModel
class MyPageProfileViewModel @Inject constructor(
    private val getProfileUseCase: getProfileUseCase,
    private val validateNickNameAuthUseCase: ValidateNickNameAuthUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
) : ViewModel() {

    private var _nickname: String = ""
    val nickname: String get() = _nickname

    private val _profileImage = SingleLiveEvent<Uri>()
    val profileImage: LiveData<Uri>
        get() = _profileImage

    private val _profileImageMultipartBodyLiveData = SingleLiveEvent<MultipartBody.Part>()
    val profileImageMultipartBodyLiveData: LiveData<MultipartBody.Part>
        get() = _profileImageMultipartBodyLiveData

    fun initData() {
        val profile = getProfileUseCase()
        _nickname = profile.nickName
        _profileImage.setValue(Uri.parse(profile.profileImage))
    }

    fun setUserImageUri(imageUri: Uri) {
        _profileImage.setValue(imageUri)
    }

    fun setUserImageUriToMultipart(imageUri: MultipartBody.Part) {
        _profileImageMultipartBodyLiveData.postValue(imageUri)
    }

    fun setNickName(nicknameValue: String) {
        _nickname = nicknameValue
    }

    private val _validateNickname = SingleLiveEvent<ViewState<Void>>()
    val validateNickname: LiveData<ViewState<Void>> get() = _validateNickname

    fun validateNickname(nickname: String) = viewModelScope.launch {
        _validateNickname.postValue(ViewState.Loading())
        when (val result = validateNickNameAuthUseCase(nickname)) {
            is ViewState.Success -> {
                _validateNickname.postValue(result)
            }
            is ViewState.Error -> {
                _validateNickname.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }

    private val _updateProfileLiveData = SingleLiveEvent<ViewState<ProfileDomainModel>>()
    val updateProfileLiveData: LiveData<ViewState<ProfileDomainModel>> get() = _updateProfileLiveData

    fun updateProfile() = viewModelScope.launch {
        _updateProfileLiveData.postValue(ViewState.Loading())
        when (val result =
            updateProfileUseCase(
                profileImageMultipartBodyLiveData.value,
                nickname.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            )) {
            is ViewState.Success -> {
                _updateProfileLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _updateProfileLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }

    private val _profileImageState = SingleLiveEvent<Boolean>()
    val profileImageState: LiveData<Boolean>
        get() = _profileImageState

    private val _nicknameState = SingleLiveEvent<Boolean>()
    val nicknameState: LiveData<Boolean>
        get() = _nicknameState

    init {
        _profileImageState.postValue(false)
        _nicknameState.postValue(true)
    }

    fun nicknameWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                _nicknameState.postValue(false)
            }

            override fun afterTextChanged(s: Editable) {}
        }
    }

    fun setNicknameState(state: Boolean) = _nicknameState.setValue(state)
}