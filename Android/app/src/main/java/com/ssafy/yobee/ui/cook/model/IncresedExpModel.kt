package com.ssafy.yobee.ui.cook.model

import com.ssafy.domain.model.user.IncreaseExpDomainModel

data class IncresedExpModel(
    val expList: ArrayList<Float>,
    val upCategory: String,
    val upExp: Int,
)

fun IncreaseExpDomainModel.toIncreasedExpModel() = IncresedExpModel(
    expList = arrayListOf(
        this.soupExp,
        this.noodleExp,
        this.dessertExp,
        this.grilledExp,
        this.sideExp
    ),
    upCategory = this.upCategory,
    upExp = this.upExp
)