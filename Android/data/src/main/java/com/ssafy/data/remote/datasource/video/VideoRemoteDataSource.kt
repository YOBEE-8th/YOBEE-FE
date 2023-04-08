package com.ssafy.data.remote.datasource.video

import com.ssafy.data.remote.datasource.video.dto.VideoResponseDto
import retrofit2.Response

internal interface VideoRemoteDataSource {
    suspend fun getVideos(
        part: String,
        maxResults: Int,
        q: String,
        key: String,
    ): Response<VideoResponseDto>
}