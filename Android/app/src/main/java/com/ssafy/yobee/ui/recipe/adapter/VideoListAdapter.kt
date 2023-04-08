package com.ssafy.yobee.ui.recipe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.ListVideoItemBinding
import com.ssafy.yobee.ui.recipe.model.Video

class VideoListAdapter(val context: Context, private val videos: ArrayList<Video>) :
    RecyclerView.Adapter<VideoListAdapter.VideoListViewHolder>() {

    interface ItemClickListener {
        fun onClick(view: View, position: Int, videoId: String)
    }

    private lateinit var itemClickListener: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    inner class VideoListViewHolder(val binding: ListVideoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(video: Video, position: Int) {
            binding.apply {
                Glide.with(context)
                    .load(video.thumbnail)
                    .placeholder(R.drawable.shimmer)
                    .into(ivVideoThumbnail)
                tvVideoTitle.text = video.title
                tvVideoChannel.text = video.channel
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoListViewHolder {
        return VideoListViewHolder(
            ListVideoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VideoListViewHolder, position: Int) {
        holder.apply {
            bind(videos[position], position)

            itemView.setOnClickListener {
                itemClickListener.onClick(it, position, videos[position].videoId)
            }
        }
    }

    override fun getItemCount(): Int {
        return videos.size
    }
}