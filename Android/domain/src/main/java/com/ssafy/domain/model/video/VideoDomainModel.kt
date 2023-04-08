package com.ssafy.domain.model.video

data class VideoDomainModel(
    val items: List<ItemDomain>,
)

data class ItemDomain(
    val id: IdDomain,
    val snippet: SnippetDomain,
)

data class IdDomain(
    val videoId: String,
)

data class SnippetDomain(
    val channelTitle: String,
    val thumbnails: ThumbnailsDomain,
    val title: String,
)

data class ThumbnailsDomain(
    val high: HighDomain,
)

data class HighDomain(
    val url: String,
)