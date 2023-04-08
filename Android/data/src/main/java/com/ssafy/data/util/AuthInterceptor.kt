package com.ssafy.data.util

import com.ssafy.data.local.prefs.AuthSharePreference
import okhttp3.Interceptor
import okhttp3.Response

internal class AuthInterceptor(private val authSharePreference: AuthSharePreference) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer " + authSharePreference.accessToken).build()
        return chain.proceed(request)
    }
}