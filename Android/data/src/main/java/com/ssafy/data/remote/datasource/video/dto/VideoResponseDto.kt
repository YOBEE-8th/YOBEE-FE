package com.ssafy.data.remote.datasource.video.dto

data class VideoResponseDto(
    val items: List<Item>,
)

data class Item(
    val id: Id,
    val snippet: Snippet,
)

data class Id(
    val videoId: String,
)

data class Snippet(
    val channelTitle: String,
    val thumbnails: Thumbnails,
    val title: String,
)

data class Thumbnails(
    val high: High,
)

data class High(
    val height: Int,
    val url: String,
    val width: Int,
)