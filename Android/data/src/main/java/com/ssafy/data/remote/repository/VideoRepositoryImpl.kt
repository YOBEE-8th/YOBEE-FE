package com.ssafy.data.remote.repository

import com.ssafy.data.remote.datasource.video.VideoRemoteDataSource
import com.ssafy.data.remote.mappers.toDomainModel
import com.ssafy.domain.model.video.VideoDomainModel
import com.ssafy.domain.repository.VideoRepository
import com.ssafy.domain.utils.ViewState
import javax.inject.Inject

internal class VideoRepositoryImpl @Inject constructor(
    private val videoRemoteDataSource: VideoRemoteDataSource,
) : VideoRepository {
    override suspend fun getVideos(
        part: String,
        maxResults: Int,
        q: String,
        key: String,
    ): ViewState<VideoDomainModel> {
        return try {
            val response = videoRemoteDataSource.getVideos(part, maxResults, q, key)
            if (response.isSuccessful) {
                if (response.body() != null) {
                    ViewState.Success("유튜브 api 성공", response.body()!!.toDomainModel())
                } else {
                    ViewState.Success("유튜브 api 성공", VideoDomainModel(listOf()))
                }
            } else {
                ViewState.Error("유튜브 api 실패", null)
            }
        } catch (e: java.lang.Exception) {
            ViewState.Error(e.message, null)
        }
    }
}