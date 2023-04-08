package com.ssafy.data.local.prefs

import android.content.Context
import android.content.SharedPreferences

private const val TAG = "FcmSharePreference_요비"

internal class FcmSharePreference(context: Context) {
    var preferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        const val SHARED_PREFERENCES_NAME = "fcm_prefs"
        const val FCM_TOKEN = "fcm_token"
    }

    fun addFcmToken(fcmToken: String) {
        val editor = preferences.edit()
        editor.putString(FCM_TOKEN, fcmToken)
        editor.apply()
    }

    fun getFcmToken(): String {
        return preferences.getString(FCM_TOKEN, "").toString()
    }

    fun deleteFcmToken() {
        preferences.edit().remove(FCM_TOKEN).apply()
    }
}