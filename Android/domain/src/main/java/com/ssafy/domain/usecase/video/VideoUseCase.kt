package com.ssafy.domain.usecase.video

import com.ssafy.domain.repository.VideoRepository
import javax.inject.Inject

class VideoUseCase @Inject constructor(private val videoRepository: VideoRepository) {
    suspend operator fun invoke(part: String, maxResults: Int, q: String, key: String) =
        videoRepository.getVideos(part, maxResults, q, key)
}