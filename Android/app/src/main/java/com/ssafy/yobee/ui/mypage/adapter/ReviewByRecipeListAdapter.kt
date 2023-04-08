package com.ssafy.yobee.ui.mypage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.ListReviewByRecipeItemBinding
import com.ssafy.yobee.ui.mypage.model.ReviewByRecipeModel

class ReviewByRecipeListAdapter(
    val context: Context,
    private val reviews: List<ReviewByRecipeModel>,
) : RecyclerView.Adapter<ReviewByRecipeListAdapter.ReviewByRecipeListViewHolder>() {

    interface ItemClickListener {
        fun onClick(view: View, position: Int, recipeId: Long)
    }

    private lateinit var itemClickListener: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    inner class ReviewByRecipeListViewHolder(val binding: ListReviewByRecipeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: ReviewByRecipeModel, position: Int) {
            binding.apply {
                Glide.with(context)
                    .load(review.img)
                    .placeholder(R.drawable.shimmer)
                    .into(ivReviewByRecipe)

                val maxLength = 8
                if (review.title.length > maxLength) {
                    tvReviewByRecipeTitle.text = "${review.title.substring(0, maxLength)}..."
                } else {
                    tvReviewByRecipeTitle.text = review.title
                }

                tvReviewByRecipeCnt.text = review.reviewCnt.toString()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ReviewByRecipeListViewHolder {
        return ReviewByRecipeListViewHolder(
            ListReviewByRecipeItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ReviewByRecipeListViewHolder, position: Int) {
        holder.apply {
            bind(reviews[position], position)

            itemView.setOnClickListener {
                itemClickListener.onClick(it, position, reviews[position].recipeId)
            }
        }
    }

    override fun getItemCount(): Int {
        return reviews.size
    }
}