package com.ssafy.domain.model.account

data class KakaoAccountDomainModel(
    val id: Long,
    val nickName: String,
    val profileImage: String,
    val email: String?,
)
