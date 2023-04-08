package com.ssafy.domain.model.account


data class Join(
    val email: String,
    val password: String,
    val nickname: String,
    val type: Int,
    val fcmToken: String,
    val profileImgUrl: String?,
)