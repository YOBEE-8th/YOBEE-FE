package com.ssafy.data.remote.di

import com.ssafy.data.local.prefs.AccountSharePreference
import com.ssafy.data.local.prefs.AuthSharePreference
import com.ssafy.data.local.prefs.FcmSharePreference
import com.ssafy.data.local.prefs.SearchKeywordPreference
import com.ssafy.data.remote.datasource.account.AccountRemoteDataSource
import com.ssafy.data.remote.datasource.auth.AuthRemoteDataSource
import com.ssafy.data.remote.datasource.cook.CookDataSource
import com.ssafy.data.remote.datasource.recipe.RecipeRemoteDataSource
import com.ssafy.data.remote.datasource.recommend.RecommendRemoteDataSource
import com.ssafy.data.remote.datasource.review.ReviewRemoteDataSource
import com.ssafy.data.remote.datasource.user.UserRemoteDataSource
import com.ssafy.data.remote.datasource.video.VideoRemoteDataSource
import com.ssafy.data.remote.datasource.voice.VoiceRemoteDataSource
import com.ssafy.data.remote.repository.*
import com.ssafy.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object RepositoryModule {
    @Provides
    @Singleton
    fun provideUserRepository(
        userRemoteDataSource: UserRemoteDataSource,
    ): UserRepository =
        UserRepositoryImpl(userRemoteDataSource)

    @Provides
    @Singleton
    fun provideFcmRepository(
        fcmSharePreference: FcmSharePreference,
    ): FcmRepository =
        FcmRepositoryImpl(fcmSharePreference)

    @Provides
    @Singleton
    fun provideAccountRepository(
        accountRemoteDataSource: AccountRemoteDataSource,
        authSharePreference: AuthSharePreference,
        accountSharePreference: AccountSharePreference,
    ): AccountRepository =
        AccountRepositoryImpl(accountRemoteDataSource, authSharePreference, accountSharePreference)

    @Provides
    @Singleton
    fun provideVideoRepository(
        videoRemoteDataSource: VideoRemoteDataSource,
    ): VideoRepository =
        VideoRepositoryImpl(videoRemoteDataSource)

    @Provides
    @Singleton
    fun provideRecipeRepository(
        recipeRemoteDataSource: RecipeRemoteDataSource,
    ): RecipeRepository = RecipeRepositoryImpl(recipeRemoteDataSource)

    @Provides
    @Singleton
    fun provideReviewRepository(
        reviewRemoteDataSource: ReviewRemoteDataSource,
    ): ReviewRepository = ReviewRepositoryImpl(reviewRemoteDataSource)

    @Provides
    @Singleton
    fun provideAuthRepository(
        authSharePreference: AuthSharePreference,
        authRemoteDataSource: AuthRemoteDataSource,
        accountSharePreference: AccountSharePreference,
    ): AuthRepository =
        AuthRepositoryImpl(authSharePreference, authRemoteDataSource, accountSharePreference)

    @Provides
    @Singleton
    fun provideSearchHistoryRepository(
        searchKeywordPreference: SearchKeywordPreference,
    ): SearchHistoryRepository = SearchHistoryRepositoryImpl(searchKeywordPreference)

    @Provides
    @Singleton
    fun provideCookRepository(
        cookDataSource: CookDataSource,
    ): CookRepository = CookRepositoryImpl(cookDataSource)

    @Provides
    @Singleton
    fun provideVoiceRepository(
        voiceRemoteDataSource: VoiceRemoteDataSource,
    ): VoiceRepository = VoiceRepositoryImpl(voiceRemoteDataSource)

    @Provides
    @Singleton
    fun provideRecommendRepository(
        recommendRemoteDataSource: RecommendRemoteDataSource,
    ): RecommendRepository = RecommendRepositoryImpl(recommendRemoteDataSource)
}