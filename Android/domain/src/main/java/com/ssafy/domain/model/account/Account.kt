package com.ssafy.domain.model.account

data class Account @JvmOverloads constructor(
    val email: String = "",
    val password: String = "",
    val nickname: String = "",
    val profileImgUrl: String? = "",
    var verificationCode: String = "",
    var type: Int = 0,
    var emailCheck: Int = 0,
    var passwordCheck: Int = 0,
    var nicknameCheck: Int = 0,
)