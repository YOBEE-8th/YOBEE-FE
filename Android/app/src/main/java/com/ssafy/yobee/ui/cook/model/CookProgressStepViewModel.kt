package com.ssafy.yobee.ui.cook.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.model.cook.CookProgressStepDomainModel

class CookProgressStepViewModel : ViewModel() {

    private val _timer = SingleLiveEvent<Int>()
    val timer: LiveData<Int> get() = _timer

    private var _fire = 0
    val fire: Int get() = _fire

    private var _description = ""
    val description: String get() = _description

    private var _descriptionImage = ""
    val descriptionImage: String get() = _descriptionImage

    fun setTimer(time: Int) {
        _timer.postValue(time)
    }

    fun initData(cookProgressStepDomainModel: CookProgressStepDomainModel) {
        _timer.postValue(cookProgressStepDomainModel.timer)
        _fire = cookProgressStepDomainModel.fire
        _description = cookProgressStepDomainModel.description
        _descriptionImage = cookProgressStepDomainModel.stepImg
        Log.d("TAG", "initData: ${_timer}")
    }
}