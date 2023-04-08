package com.ssafy.domain.repository

import com.ssafy.domain.model.video.VideoDomainModel
import com.ssafy.domain.utils.ViewState

interface VideoRepository {
    suspend fun getVideos(
        part: String,
        maxResults: Int,
        q: String,
        key: String,
    ): ViewState<VideoDomainModel>
}