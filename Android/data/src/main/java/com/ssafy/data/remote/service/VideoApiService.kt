package com.ssafy.data.remote.service

import com.ssafy.data.remote.datasource.video.dto.VideoResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface VideoApiService {
    @GET("search")
    suspend fun getVideos(
        @Query("part") part: String,
        @Query("maxResults") maxResults: Int,
        @Query("q") q: String,
        @Query("key") key: String,
    ): Response<VideoResponseDto>
}