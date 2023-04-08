package com.ssafy.data.local.prefs

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.util.Log

private const val TAG = "AccountSharePreference_요비"

internal class AccountSharePreference(context: Context) {
    var preferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    companion object {
        const val SHARED_PREFERENCES_NAME = "account_prefs"
        const val NICKNAME = "nickname"
        const val PROFILE_IMAGE = "profileImage"
        const val TYPE = "type"
        const val AUTO_LOGIN = "autoLogin"
    }

    var nickname: String?
        get() = preferences.getString(NICKNAME, null)
        set(value) {
            preferences.edit().putString(NICKNAME, value).apply()
        }

    var profileImage: String?
        get() = preferences.getString(PROFILE_IMAGE, null)
        set(value) {
            preferences.edit().putString(PROFILE_IMAGE, value).apply()
        }

    var type: String?
        get() = preferences.getString(TYPE, null)
        set(value) {
            preferences.edit().putString(TYPE, value).apply()
        }

    var autoLogin: String?
        get() = preferences.getString(AUTO_LOGIN, "0")
        set(value) {
            preferences.edit().putString(AUTO_LOGIN, value).apply()
        }

    fun clearAccountPreference(): Boolean {
        return try {
            preferences.edit().clear().apply()
            Log.d(
                ContentValues.TAG,
                "clearAuthPreference: Logout Success. nickname : $nickname,  profileImage : $profileImage type : $type"
            )
            true
        } catch (e: Exception) {
            Log.d(ContentValues.TAG, "clearAuthPreference: Logout Fail : ${e.message}")
            false
        }
    }
}