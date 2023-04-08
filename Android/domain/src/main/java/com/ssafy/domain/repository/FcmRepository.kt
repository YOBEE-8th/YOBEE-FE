package com.ssafy.domain.repository

interface FcmRepository {
    fun addFcmToken(fcmToken: String)
    fun getFcmToken(): String
}