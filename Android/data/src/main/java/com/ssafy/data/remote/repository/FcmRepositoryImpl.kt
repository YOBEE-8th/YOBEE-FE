package com.ssafy.data.remote.repository

import com.ssafy.data.local.prefs.FcmSharePreference
import com.ssafy.domain.repository.FcmRepository
import javax.inject.Inject

internal class FcmRepositoryImpl @Inject constructor(
    private val fcmSharePreference: FcmSharePreference,
) :
    FcmRepository {
    override fun addFcmToken(fcmToken: String) =
        fcmSharePreference.addFcmToken(fcmToken)

    override fun getFcmToken(): String =
        fcmSharePreference.getFcmToken()

}