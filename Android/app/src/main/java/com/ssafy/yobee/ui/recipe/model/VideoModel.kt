package com.ssafy.yobee.ui.recipe.model

import com.ssafy.domain.model.video.ItemDomain
import com.ssafy.domain.model.video.VideoDomainModel

data class VideoModel(
    val videos: ArrayList<Video>,
)

data class Video(
    val videoId: String,
    val thumbnail: String,
    val title: String,
    val channel: String,
)

fun VideoDomainModel.toVideoModel() = VideoModel(
    videos = getVideos(this.items)
)

fun getVideos(items: List<ItemDomain>): ArrayList<Video> {
    val res = arrayListOf<Video>()

    items.forEach { item ->
        res.add(
            Video(
                item.id.videoId,
                item.snippet.thumbnails.high.url,
                item.snippet.title,
                item.snippet.channelTitle
            )
        )
    }

    return res
}
