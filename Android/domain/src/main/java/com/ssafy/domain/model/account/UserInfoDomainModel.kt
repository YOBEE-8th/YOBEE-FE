package com.ssafy.domain.model.account

data class UserInfoDomainModel(
    val accessToken: String,
    val refreshToken: String,
    val nickName: String,
    val profileImage: String?,
    val type: Int,
)