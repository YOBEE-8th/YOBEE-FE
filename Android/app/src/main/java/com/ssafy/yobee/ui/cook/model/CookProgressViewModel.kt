package com.ssafy.yobee.ui.cook.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.model.cook.CookProgressStepDomainModel
import com.ssafy.domain.model.review.CreateReviewDomainModel
import com.ssafy.domain.model.voice.VoiceDomainModel
import com.ssafy.domain.usecase.cook.GetCookProgressListUseCase
import com.ssafy.domain.usecase.review.CreateReviewUseCase
import com.ssafy.domain.usecase.user.IncreaseExpUseCase
import com.ssafy.domain.usecase.voice.GetAnswerUseCase
import com.ssafy.domain.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class CookProgressViewModel @Inject constructor(
    private val getCookProgressListUseCase: GetCookProgressListUseCase,
    private val createReviewUseCase: CreateReviewUseCase,
    private val increaseExpUseCase: IncreaseExpUseCase,
    private val getAnswerUseCase: GetAnswerUseCase,
) :
    ViewModel() {
    private var _recipeId = -1
    val recipeId: Int get() = _recipeId

    private var _isSpeaking = SingleLiveEvent<Boolean>()
    val isSpeaking: LiveData<Boolean> get() = _isSpeaking

    private var _voiceCode = ""
    val voiceCode: String get() = _voiceCode

    private var _uri = ""
    val uri: String get() = _uri

    private var _reviewId = -1
    val reviewId: Int get() = _reviewId

    private var _sttStatus = MutableLiveData<Boolean>()
    val sttStatus: LiveData<Boolean> get() = _sttStatus

    private val _cookProgressList = SingleLiveEvent<ViewState<List<CookProgressStepDomainModel>>>()
    val cookProgressList: LiveData<ViewState<List<CookProgressStepDomainModel>>> get() = _cookProgressList

    private val _timerTtsText = SingleLiveEvent<String>()
    val timerTtsText: LiveData<String> get() = _timerTtsText

    private var _currentPosition = 0
    val currentPosition: Int get() = _currentPosition

    fun setCurrentPosition(position: Int) {
        _currentPosition = position
    }

    fun setTimerTtsText(text: String) {
        _timerTtsText.postValue(text)
    }

    fun setSttStatusValue(status: Boolean) {
        _sttStatus.value = status
    }

    fun getSttStatusValue() = sttStatus.value!!

    fun setRecipeId(recipeId: Int) {
        this._recipeId = recipeId
    }

    fun setVoiceCode(voiceCode: String) {
        _voiceCode = voiceCode
    }

    fun setUri(uri: String) {
        _uri = uri
    }

    fun setReviewId(reviewId: Int) {
        _reviewId = reviewId
    }

    fun getCookProgressList() = viewModelScope.launch {
        _cookProgressList.postValue(ViewState.Loading())
        when (val response = getCookProgressListUseCase(recipeId)) {
            is ViewState.Error -> {
                _cookProgressList.postValue(response)
            }
            is ViewState.Loading -> {}
            is ViewState.Success -> {
                _cookProgressList.postValue(response)
            }
        }
    }

    private val _createReviewLiveData = SingleLiveEvent<ViewState<CreateReviewDomainModel>>()
    val createReviewLiveData: LiveData<ViewState<CreateReviewDomainModel>>
        get() = _createReviewLiveData

    fun createReview(img: MultipartBody.Part, recipeId: Int, content: String) =
        viewModelScope.launch {
            _createReviewLiveData.postValue(ViewState.Loading())
            val reviewHashMap = HashMap<String, RequestBody>()
            reviewHashMap["recipeId"] =
                recipeId.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            reviewHashMap["content"] =
                content.toRequestBody("multipart/form-data".toMediaTypeOrNull())

            when (val result = createReviewUseCase(img, reviewHashMap)) {
                is ViewState.Success -> {
                    _createReviewLiveData.postValue(result)
                }
                is ViewState.Error -> {
                    _createReviewLiveData.postValue(result)
                }
                is ViewState.Loading -> {}
            }
        }

    private val _increaseExpLiveData = SingleLiveEvent<ViewState<IncresedExpModel>>()
    val increaseExpLiveData: LiveData<ViewState<IncresedExpModel>>
        get() = _increaseExpLiveData

    fun increaseExp(recipeId: Int) = viewModelScope.launch {
        _increaseExpLiveData.postValue(ViewState.Loading())

        when (val response = increaseExpUseCase(recipeId)) {
            is ViewState.Success -> {
                _increaseExpLiveData.postValue(
                    ViewState.Success(
                        response.message,
                        response.value!!.toIncreasedExpModel()
                    )
                )
            }
            is ViewState.Error -> {
                _increaseExpLiveData.postValue(ViewState.Error(response.message))
            }
            is ViewState.Loading -> {}
        }
    }

    private val _getAnswerLiveData = SingleLiveEvent<ViewState<VoiceDomainModel>>()
    val getAnswerLiveData: LiveData<ViewState<VoiceDomainModel>> get() = _getAnswerLiveData

    fun getAnswer(message: String) = viewModelScope.launch {
        _getAnswerLiveData.postValue(ViewState.Loading())
        when (val response = getAnswerUseCase(recipeId, message)) {
            is ViewState.Success -> {
                _getAnswerLiveData.postValue(response)
            }
            is ViewState.Error -> {
                _getAnswerLiveData.postValue(response)
            }
            is ViewState.Loading -> {

            }
        }
    }

    private var _voiceName: String = "유리"
    val voiceName: String get() = _voiceName

    fun setVoiceName(voiceNameValue: String) {
        _voiceName = voiceNameValue
    }

    private var _isKeywordInActivate: Boolean = false
    val isKeywordInActivate: Boolean get() = _isKeywordInActivate

    fun setKeywordExist(status: Boolean) {
        _isKeywordInActivate = status
    }
}