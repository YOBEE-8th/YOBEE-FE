package com.ssafy.yobee.ui.cook.model

import com.ssafy.domain.model.cook.CookProgressStepDomainModel

data class CookProgressStepUIModel(
    val fire: Int,
    var timer: Int,
    val stepImg: String,
    val description: String,
)

fun CookProgressStepDomainModel.toUIModel() =
    CookProgressStepUIModel(fire, timer, stepImg, description)