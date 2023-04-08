package com.ssafy.data.remote.di

import com.ssafy.data.remote.service.*
import com.ssafy.data.util.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ServiceModule {
    @Provides
    @Singleton
    fun provideAuthService(
        @NoAuthInterceptorClient retrofit: Retrofit,
    ): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApiService(@AuthInterceptorClient retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun provideVideoApiService(@VideoInterceptorClient videoRetrofit: Retrofit): VideoApiService =
        videoRetrofit.create(VideoApiService::class.java)

    @Provides
    @Singleton
    fun provideRecipeApiService(@AuthInterceptorClient retrofit: Retrofit): RecipeApiService =
        retrofit.create(RecipeApiService::class.java)

    @Provides
    @Singleton
    fun provideAccountApiService(@NoAuthInterceptorClient retrofit: Retrofit): AccountApiService =
        retrofit.create(AccountApiService::class.java)

    @Provides
    @Singleton
    fun provideAccountAuthApiService(@AuthInterceptorClient retrofit: Retrofit): AccountAuthApiService =
        retrofit.create(AccountAuthApiService::class.java)

    @Provides
    @Singleton
    fun provideReviewApiService(@AuthInterceptorClient retrofit: Retrofit): ReviewApiService =
        retrofit.create(ReviewApiService::class.java)

    @Provides
    @Singleton
    fun provideCookApiService(@AuthInterceptorClient retrofit: Retrofit): CookApiService =
        retrofit.create(CookApiService::class.java)

    @Provides
    @Singleton
    fun provideVoiceApiService(@VoiceInterceptorClient retrofit: Retrofit): VoiceApiService =
        retrofit.create(VoiceApiService::class.java)

    @Provides
    @Singleton
    fun provideRecommendApiService(@AuthInterceptorClient retrofit: Retrofit): RecommendApiService =
        retrofit.create(RecommendApiService::class.java)

    @Provides
    @Singleton
    fun provideKakaoApiService(@KakaoInterceptorClient retrofit: Retrofit): KakaoApiService =
        retrofit.create(KakaoApiService::class.java)
}