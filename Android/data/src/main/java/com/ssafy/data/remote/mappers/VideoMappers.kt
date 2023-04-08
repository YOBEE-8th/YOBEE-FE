package com.ssafy.data.remote.mappers

import com.ssafy.data.remote.datasource.video.dto.*
import com.ssafy.domain.model.video.*

internal fun VideoResponseDto.toDomainModel() = VideoDomainModel(
    items = getItemList(this.items)
)

internal fun Id.toDomainModel() = IdDomain(
    videoId = this.videoId
)

internal fun Snippet.toDomainModel() = SnippetDomain(
    channelTitle = this.channelTitle,
    thumbnails = this.thumbnails.toDomainModel(),
    title = this.title
)

internal fun Thumbnails.toDomainModel() = ThumbnailsDomain(
    high = this.high.toDomainModel()
)

internal fun High.toDomainModel() = HighDomain(
    url = this.url
)

fun getItemList(items: List<Item>): List<ItemDomain> {
    val res = arrayListOf<ItemDomain>()

    items.forEach { item ->
        res.add(ItemDomain(item.id.toDomainModel(), item.snippet.toDomainModel()))
    }

    return res
}