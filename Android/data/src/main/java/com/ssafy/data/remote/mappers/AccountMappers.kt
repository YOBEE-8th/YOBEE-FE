package com.ssafy.data.remote.mappers

import com.ssafy.data.remote.datasource.account.dto.AccountResponseDto
import com.ssafy.data.remote.datasource.account.dto.UserResponseDto
import com.ssafy.data.remote.datasource.auth.dto.KakaoAuthResponseDto
import com.ssafy.domain.model.account.*

internal fun UserResponseDto.toDomainModel() = UserInfoDomainModel(
    accessToken = this.accessToken,
    refreshToken = this.refreshToken,
    nickName = this.nickName,
    profileImage = this.profileImage,
    type = this.type
)

internal fun AccountResponseDto.toDomainModel() = AccountnfoDomainModel(
    accessToken = this.accessToken,
    refreshToken = this.refreshToken,
    nickName = this.nickName,
    profileImage = this.profileImage,
    type = this.type
)

internal fun AccountResponseDto.toLogoutDomainModel() = LogoutDomainModel(
    type = this.type
)

internal fun AccountResponseDto.toWithdrawalModel() = WithdrawalDomainModel(
    email = this.email
)

internal fun AccountResponseDto.toProfileDomainModel() = ProfileDomainModel(
    nickName = this.nickName,
    profileImage = this.profileImage
)

internal fun KakaoAuthResponseDto.toKakaoAccountDomainModel() = KakaoAccountDomainModel(
    id = this.id,
    nickName = this.properties?.nickname ?: "",
    profileImage = this.properties?.profile_image ?: "",
    email = this.kakaoAccount?.email ?: ""
)