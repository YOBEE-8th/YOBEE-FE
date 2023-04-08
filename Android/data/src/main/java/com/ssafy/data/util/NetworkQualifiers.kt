package com.ssafy.data.util

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NoAuthInterceptorClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VideoInterceptorClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class VoiceInterceptorClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoInterceptorClient