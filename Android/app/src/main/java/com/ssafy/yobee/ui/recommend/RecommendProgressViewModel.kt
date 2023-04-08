package com.ssafy.yobee.ui.recommend

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.domain.model.recommend.RecommendDomainModel
import com.ssafy.domain.usecase.recommend.GetRecommendListUseCase
import com.ssafy.domain.utils.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendProgressViewModel @Inject constructor(private val getRecommendListUseCase: GetRecommendListUseCase) :
    ViewModel() {
    var progress = 0
    private var currentRoundMenuIndex = 0

    private val _gameEnd = MutableLiveData<Boolean>()
    val gameEnd: LiveData<Boolean> get() = _gameEnd

    private val _selectedMenu = MutableLiveData<RecommendDomainModel>()
    val selectedMenu: LiveData<RecommendDomainModel> get() = _selectedMenu

    private val _recommendList = MutableLiveData<ViewState<List<RecommendDomainModel>>>()
    val recommendList: LiveData<ViewState<List<RecommendDomainModel>>> get() = _recommendList

    private val _upsideMenu = MutableLiveData<RecommendDomainModel>()
    val upsideMenu: LiveData<RecommendDomainModel> get() = _upsideMenu

    private val _downsideMenu = MutableLiveData<RecommendDomainModel>()
    val downsideMenu: LiveData<RecommendDomainModel> get() = _downsideMenu

    private val _nextRoundMenu = mutableListOf<RecommendDomainModel>()

    fun setUpsideMenu() {
        _upsideMenu.value = _recommendList.value!!.value!![currentRoundMenuIndex]
    }

    fun setDownsideMenu() {
        _downsideMenu.value = _recommendList.value!!.value!![currentRoundMenuIndex + 1]
    }

    fun upsideMenuClick() {
        // 다음 라운드 진출
        _selectedMenu.value = _upsideMenu.value!!
        _nextRoundMenu.add(_upsideMenu.value!!)
        progressNextRound()

    }

    fun downsideMenuClick() {
        // 다음 라운드 진출
        _selectedMenu.value = _downsideMenu.value!!
        _nextRoundMenu.add(_downsideMenu.value!!)
        progressNextRound()
    }

    fun progressNextRound() {
        progress += 1
        currentRoundMenuIndex += 2
        if (currentRoundMenuIndex >= _recommendList.value!!.value!!.size) {
            currentRoundMenuIndex = 0
            if (_nextRoundMenu.size > 1) {
                _recommendList.value =
                    ViewState.Success("next round!", _nextRoundMenu.toMutableList())
                _nextRoundMenu.clear()
                Log.d("TAG", "progressNextRound: 새로운 라운드!")
            } else {
                _gameEnd.value = true
                Log.d("TAG", "progressNextRound: 끝!")
            }
        }
        setUpsideMenu()
        setDownsideMenu()

    }

    fun getRecommendList() = viewModelScope.launch {
        _recommendList.value = ViewState.Loading()
        getRecommendListUseCase().run {
            when (this) {
                is ViewState.Success -> {
                    _recommendList.value = this
                }
                is ViewState.Error -> {
                    _recommendList.value = this
                }
                is ViewState.Loading -> {
                }
            }
        }
    }
}