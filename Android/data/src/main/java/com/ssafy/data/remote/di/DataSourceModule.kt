package com.ssafy.data.remote.di

import com.ssafy.data.remote.datasource.account.AccountRemoteDataSource
import com.ssafy.data.remote.datasource.account.AccountRemoteDataSourceImpl
import com.ssafy.data.remote.datasource.auth.AuthRemoteDataSource
import com.ssafy.data.remote.datasource.auth.AuthRemoteDataSourceImpl
import com.ssafy.data.remote.datasource.cook.CookDataSource
import com.ssafy.data.remote.datasource.cook.CookDataSourceImpl
import com.ssafy.data.remote.datasource.recipe.RecipeRemoteDataSource
import com.ssafy.data.remote.datasource.recipe.RecipeRemoteDataSourceImpl
import com.ssafy.data.remote.datasource.recommend.RecommendRemoteDataSource
import com.ssafy.data.remote.datasource.recommend.RecommendRemoteDataSourceImpl
import com.ssafy.data.remote.datasource.review.ReviewRemoteDataSource
import com.ssafy.data.remote.datasource.review.ReviewRemoteDataSourceImpl
import com.ssafy.data.remote.datasource.user.UserRemoteDataSource
import com.ssafy.data.remote.datasource.user.UserRemoteDataSourceImpl
import com.ssafy.data.remote.datasource.video.VideoRemoteDataSource
import com.ssafy.data.remote.datasource.video.VideoRemoteDataSourceImpl
import com.ssafy.data.remote.datasource.voice.VoiceRemoteDataSource
import com.ssafy.data.remote.datasource.voice.VoiceRemoteDataSourceImpl
import com.ssafy.data.remote.service.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataSourceModule {
    @Provides
    @Singleton
    fun provideUserRemoteDataSource(userApiService: UserApiService): UserRemoteDataSource =
        UserRemoteDataSourceImpl(userApiService)

    @Provides
    @Singleton
    fun provideRecipeRemoteDataSource(recipeApiService: RecipeApiService): RecipeRemoteDataSource =
        RecipeRemoteDataSourceImpl(recipeApiService)

    @Provides
    @Singleton
    fun provideAccountRemoteDataSource(
        accountApiService: AccountApiService,
        accountAuthApiService: AccountAuthApiService,
        kakaoApiService: KakaoApiService,
    ): AccountRemoteDataSource =
        AccountRemoteDataSourceImpl(accountApiService, accountAuthApiService, kakaoApiService)

    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(authApiService: AuthApiService): AuthRemoteDataSource =
        AuthRemoteDataSourceImpl(authApiService)

    @Provides
    @Singleton
    fun provideVideoRemoteDataSource(videoApiService: VideoApiService): VideoRemoteDataSource =
        VideoRemoteDataSourceImpl(videoApiService)

    @Provides
    @Singleton
    fun provideReviewRemoteDataSource(reviewApiService: ReviewApiService): ReviewRemoteDataSource =
        ReviewRemoteDataSourceImpl(reviewApiService)

    @Provides
    @Singleton
    fun provideCookRemoteDataSource(cookApiService: CookApiService): CookDataSource =
        CookDataSourceImpl(cookApiService)

    @Provides
    @Singleton
    fun provideVoiceRemoteDataSource(voiceApiService: VoiceApiService): VoiceRemoteDataSource =
        VoiceRemoteDataSourceImpl(voiceApiService)

    @Provides
    @Singleton
    fun provideRecommendRemoteDataSource(recommendApiService: RecommendApiService): RecommendRemoteDataSource =
        RecommendRemoteDataSourceImpl(recommendApiService)
}