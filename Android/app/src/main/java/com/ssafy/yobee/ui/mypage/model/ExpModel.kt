package com.ssafy.yobee.ui.mypage.model

import com.ssafy.domain.model.user.ExpDomainModel

data class ExpModel(
    val expList: ArrayList<Float>,
)

fun ExpDomainModel.toExpModel() = ExpModel(
    expList = arrayListOf(
        this.soupExp,
        this.noodleExp,
        this.dessertExp,
        this.grilledExp,
        this.sideExp
    )
)