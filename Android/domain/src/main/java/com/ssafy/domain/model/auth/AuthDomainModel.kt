package com.ssafy.domain.model.auth

data class AuthDomainModel(
    val accessToken: String,
    val refreshToken: String,
)