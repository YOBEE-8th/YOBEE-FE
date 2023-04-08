package com.ssafy.yobee.ui.mypage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.ListReviewByDateItemBinding
import com.ssafy.yobee.ui.mypage.model.ReviewByDateModel

private const val TAG = "ReviewByDateListAdapter_요비"

class ReviewByDateListAdapter(val context: Context, private val reviews: List<ReviewByDateModel>) :
    RecyclerView.Adapter<ReviewByDateListAdapter.ReviewByDateListViewHolder>() {

    interface ItemClickListener {
        fun onWriteBtnClick(view: View, position: Int, reviewId: Long)
        fun onCheckBtnClick(view: View, position: Int, reviewId: Long)
    }

    private lateinit var itemClickListener: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    inner class ReviewByDateListViewHolder(val binding: ListReviewByDateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: ReviewByDateModel, position: Int) {
            binding.apply {
                Glide.with(context)
                    .load(review.reviewImg)
                    .placeholder(R.drawable.shimmer)
                    .into(ivReviewByDate)

                val maxLength = 10
                if (review.title.length > maxLength) {
                    tvReviewByDateTitle.text = "${review.title.substring(0, maxLength)}..."
                } else {
                    tvReviewByDateTitle.text = review.title
                }

                tvReviewByDateDate.text = review.createdAt

                if (review.isCompleted) {
                    btnReviewByDateCheck.visibility = View.VISIBLE
                    btnReviewByDateWrite.visibility = View.GONE
                } else {
                    btnReviewByDateCheck.visibility = View.GONE
                    btnReviewByDateWrite.visibility = View.VISIBLE
                }

                btnReviewByDateWrite.setOnClickListener {
                    itemClickListener.onWriteBtnClick(it, position, review.reviewId)
                }
                btnReviewByDateCheck.setOnClickListener {
                    itemClickListener.onCheckBtnClick(it, position, review.reviewId)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ReviewByDateListViewHolder {
        return ReviewByDateListViewHolder(
            ListReviewByDateItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ReviewByDateListViewHolder, position: Int) {
        holder.apply {
            bind(reviews[position], position)
        }
    }

    override fun getItemCount(): Int {
        return reviews.size
    }
}