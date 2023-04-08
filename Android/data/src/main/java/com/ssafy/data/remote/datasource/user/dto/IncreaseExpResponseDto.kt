package com.ssafy.data.remote.datasource.user.dto

data class IncreaseExpResponseDto(
    val soupExp: Int,
    val sideExp: Int,
    val grilledExp: Int,
    val noodleExp: Int,
    val dessertExp: Int,
    val upCategory: String,
    val upExp: Int,
)