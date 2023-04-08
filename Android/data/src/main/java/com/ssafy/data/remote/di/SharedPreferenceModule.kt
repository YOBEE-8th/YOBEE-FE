package com.ssafy.data.remote.di

import android.content.Context
import com.ssafy.data.local.prefs.AccountSharePreference
import com.ssafy.data.local.prefs.AuthSharePreference
import com.ssafy.data.local.prefs.FcmSharePreference
import com.ssafy.data.local.prefs.SearchKeywordPreference

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SharedPreferenceModule {
    @Provides
    @Singleton
    fun provideSearchPreferences(@ApplicationContext context: Context): SearchKeywordPreference {
        return SearchKeywordPreference(context)
    }

    @Provides
    @Singleton
    fun provideAuthSharedPreferences(@ApplicationContext context: Context): AuthSharePreference {
        return AuthSharePreference(context)
    }

    @Provides
    @Singleton
    fun provideFcmSharedPreferences(@ApplicationContext context: Context): FcmSharePreference {
        return FcmSharePreference(context)
    }

    @Provides
    @Singleton
    fun provideAccountSharedPreferences(@ApplicationContext context: Context): AccountSharePreference {
        return AccountSharePreference(context)
    }
}