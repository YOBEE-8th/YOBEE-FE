package com.ssafy.data.remote.datasource.video

import com.ssafy.data.remote.datasource.video.dto.VideoResponseDto
import com.ssafy.data.remote.service.VideoApiService
import retrofit2.Response
import javax.inject.Inject

internal class VideoRemoteDataSourceImpl @Inject constructor(private val videoApiService: VideoApiService) :
    VideoRemoteDataSource {
    override suspend fun getVideos(
        part: String,
        maxResults: Int,
        q: String,
        key: String,
    ): Response<VideoResponseDto> {
        return videoApiService.getVideos(part, maxResults, q, key)
    }
}