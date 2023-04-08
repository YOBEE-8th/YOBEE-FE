package com.ssafy.data.remote.di

import android.content.Context
import com.ssafy.common.Constant.BASE_URL
import com.ssafy.common.Constant.BASE_URL_VOICE
import com.ssafy.common.Constant.BASE_URL_YOUTUBE
import com.ssafy.common.Constant.KAKAO_API_URL
import com.ssafy.data.local.prefs.AuthSharePreference
import com.ssafy.data.util.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    @NoAuthInterceptorClient
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(getLoggingInterceptor())
            .build()
    }

    @Provides
    @Singleton
    @AuthInterceptorClient
    fun provideAuthHttpClient(
        @ApplicationContext context: Context,
    ): OkHttpClient {
        val authInterceptor = AuthInterceptor(AuthSharePreference(context))
        return OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(getLoggingInterceptor())
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @KakaoInterceptorClient
    fun provideKakaoHttpClient(
        @ApplicationContext context: Context,
    ): OkHttpClient {
        val authInterceptor = KakaoAuthInterceptor(AuthSharePreference(context))
        return OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(getLoggingInterceptor())
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @VideoInterceptorClient
    fun provideVideoHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(getLoggingInterceptor())
            .build()
    }

    @Provides
    @Singleton
    @VoiceInterceptorClient
    fun provideVoiceHttpClient(
        @ApplicationContext context: Context,
    ): OkHttpClient {
        val authInterceptor = AuthInterceptor(AuthSharePreference(context))
        return OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(getLoggingInterceptor())
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @NoAuthInterceptorClient
    fun provideRetrofit(
        @NoAuthInterceptorClient okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    @Provides
    @Singleton
    @AuthInterceptorClient
    fun provideAuthRetrofit(
        @AuthInterceptorClient okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @VideoInterceptorClient
    fun provideVideoRetrofit(
        @VideoInterceptorClient okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL_YOUTUBE)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @VoiceInterceptorClient
    fun provideVoiceRetrofit(
        @VoiceInterceptorClient okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL_VOICE)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @KakaoInterceptorClient
    fun provideKakaoRetrofit(
        @KakaoInterceptorClient okHttpClient: OkHttpClient,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(KAKAO_API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private fun getLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
}