package com.ssafy.yobee

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.willy.ratingbar.BaseRatingBar

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("glideImage")
    fun setImage(view: ImageView, imageUrl: String?) {
        if (imageUrl == null) {
            view.setImageResource(R.drawable.ic_launcher_foreground)
        } else {
            Glide.with(view.context).load(imageUrl)
                .thumbnail(Glide.with(view).load(R.drawable.shimmer)).into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("glideListImage")
    fun setListImage(view: ImageView, imageUrl: String?) {
        if (imageUrl == null) {
            view.setImageResource(R.drawable.ic_launcher_foreground)
        } else {
            Glide.with(view.context).load(imageUrl)
                .thumbnail(Glide.with(view).load(R.drawable.shimmer)).apply(
                    RequestOptions()
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                ).override(200, 200).into(view)
        }
    }


    @JvmStatic
    @BindingAdapter("glideImageCircleCrop")
    fun setImageCircleCrop(view: ImageView, imageUrl: Uri?) {
        if (imageUrl == null) {
            view.setImageResource(R.drawable.ic_launcher_foreground)
        } else {
            Glide.with(view.context).load(imageUrl)
                .thumbnail(Glide.with(view).load(R.drawable.shimmer)).apply(
                    RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                ).circleCrop().into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("setLikeCount")
    fun setLikeCount(view: TextView, likeCount: Int) {
        view.text = likeCount.toString()
    }

    @JvmStatic
    @BindingAdapter("app:srb_rating")
    fun setRating(ratingBar: BaseRatingBar, rating: Int) {
        ratingBar.rating = rating.toFloat()
    }

    @JvmStatic
    @BindingAdapter("setSortByText")
    fun setSortByText(view: TextView, sortBy: Int) {
        when (sortBy) {
            0 -> view.text = "좋아요"
            1 -> view.text = "리뷰"
            2 -> view.text = "난이도"
        }
    }

    @JvmStatic
    @BindingAdapter("showAILabel")
    fun showAILabel(view: TextView, isAI: Boolean) {
        if (isAI) view.visibility = View.VISIBLE else view.visibility = View.GONE
    }

    @JvmStatic
    @BindingAdapter("setTimeFromSecond")
    fun setTimeFromSecond(view: TextView, millisecond: Int) {
        val hour = millisecond / 3600000
        val minute = (millisecond % 3600000) / 60000
        val hourText = if (hour < 10) {
            "0$hour"
        } else {
            "$hour"
        }
        val minuteText = if (minute < 10) {
            "0$minute"
        } else {
            "$minute"
        }
        view.text = "${hourText}:${minuteText}"
    }

    @JvmStatic
    @BindingAdapter("setMicStatus")
    fun setMicStatus(view: ImageView, status: Boolean) {
        if (status) view.setImageResource(R.drawable.ic_mic_on)
        else view.setImageResource(R.drawable.ic_mic_off)
    }

    @JvmStatic
    @BindingAdapter("setRecipeTitle")
    fun setRecipeTitle(view: TextView, title: String) {
        if (title.length > 7) {
            view.text = "${title.substring(0, 7)}..."
        } else {
            view.text = title
        }
    }
}