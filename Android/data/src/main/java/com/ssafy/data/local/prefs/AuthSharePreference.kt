package com.ssafy.data.local.prefs

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log

internal class AuthSharePreference(context: Context) {

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    var accessToken: String?
        get() = prefs.getString("accessToken", null)
        set(value) {
            Log.d("SP", "JWT_ACCESS set: ${value}")
            prefs.edit().putString("accessToken", value).apply()
        }

    var refreshToken: String?
        get() = prefs.getString("refreshToken", null)
        set(value) {
            Log.d("SP", "JWT_REFRESH set: ${value}")
            prefs.edit().putString("refreshToken", value).apply()
        }

    var kakaoAccessToken: String?
        get() = prefs.getString("kakaoAccessToken", null)
        set(value) {
            Log.d("SP", "KakaoAccessToken set: ${value}")
            prefs.edit().putString("kakaoAccessToken", value).apply()
        }

    fun clearAuthPreference(): Boolean {
        return try {
            prefs.edit().clear().apply()
            Log.d(
                TAG,
                "clearAuthPreference: Logout Success. accessToken : $accessToken,  refreshToken : $refreshToken"
            )
            true
        } catch (e: Exception) {
            Log.d(TAG, "clearAuthPreference: Logout Fail : ${e.message}")
            false
        }
    }

}