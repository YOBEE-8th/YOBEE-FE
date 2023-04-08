package com.ssafy.yobee.ui.recipe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.ListReviewItemBinding
import com.ssafy.yobee.ui.recipe.model.ReviewModel

class ReviewListAdapter(val context: Context, private val reviews: List<ReviewModel>) :
    RecyclerView.Adapter<ReviewListAdapter.ReviewListViewHolder>() {

    interface ItemClickListener {
        fun onLikeClick(view: View, position: Int, reviewId: Int)
        fun onDeleteClick(view: View, position: Int, reviewId: Int)
    }

    private lateinit var itemClickListener: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    inner class ReviewListViewHolder(val binding: ListReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: ReviewModel, position: Int) {
            binding.apply {
                Glide.with(context)
                    .load(review.profileImg)
                    .placeholder(R.drawable.shimmer)
                    .into(ivReviewProfile)

                tvReviewNickname.text = review.nickname
                tvReviewDate.text = review.createdAt

                crlReviewLike.setAttrs(review.isLike, review.likeCnt)

                ivReviewDelete.visibility = if (review.isMine) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                Glide.with(context)
                    .load(review.reviewImg)
                    .transform(CenterCrop(), RoundedCorners(8))
                    .placeholder(R.drawable.shimmer)
                    .into(ivReviewImage)

                tvReviewContent.text = review.content

                crlReviewLike.setOnClickListener {
                    itemClickListener.onLikeClick(it, position, review.reviewId)
                }
                ivReviewDelete.setOnClickListener {
                    itemClickListener.onDeleteClick(it, position, review.reviewId)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewListViewHolder {
        return ReviewListViewHolder(
            ListReviewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ReviewListViewHolder, position: Int) {
        holder.apply {
            bind(reviews[position], position)
        }
    }

    override fun getItemCount(): Int {
        return reviews.size
    }
}