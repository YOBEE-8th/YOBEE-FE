package com.ssafy.domain.model.user

data class IncreaseExpDomainModel(
    val soupExp: Float,
    val sideExp: Float,
    val grilledExp: Float,
    val noodleExp: Float,
    val dessertExp: Float,
    val upCategory: String,
    val upExp: Int,
)