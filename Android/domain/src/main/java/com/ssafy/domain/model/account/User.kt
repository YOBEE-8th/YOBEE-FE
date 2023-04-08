package com.ssafy.domain.model.account

data class User(
    val userId: Int,
    val email: String,
    val password: String,
    val type: Int,
    val fcmToken: String,
    val accessToken: String,
    val refreshToken: String,
    val nickName: String,
    val profileImage: String?,
    val level: Int,
)