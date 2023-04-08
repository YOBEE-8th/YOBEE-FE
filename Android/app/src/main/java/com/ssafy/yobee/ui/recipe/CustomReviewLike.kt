package com.ssafy.yobee.ui.recipe

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.ssafy.yobee.R

class CustomReviewLike(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    lateinit var likeImg: ImageView
    lateinit var likeCnt: TextView

    init {
        init()
        getAttrs(attrs)
    }

    private fun init() {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_review_like, this, false)
        addView(view)
        likeImg = findViewById(R.id.iv_review_like)
        likeCnt = findViewById(R.id.tv_review_like)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomReviewLike)
        setTypedArray(typedArray)
    }

    private fun setTypedArray(typedArray: TypedArray) {
        likeImg.setImageResource(
            typedArray.getResourceId(
                R.styleable.CustomReviewLike_likeImg,
                R.drawable.ic_launcher_foreground
            )
        )
        likeCnt.text = typedArray.getText(R.styleable.CustomReviewLike_likeCnt)
        typedArray.recycle()
    }

    fun setAttrs(isLike: Boolean, cnt: Int) {
        if (isLike) {
            likeImg.setImageResource(R.drawable.ic_review_like_click)
            likeCnt.setTextColor(ContextCompat.getColor(context, R.color.bittersweet_400))
        } else {
            likeImg.setImageResource(R.drawable.ic_review_like_no_click)
            likeCnt.setTextColor(ContextCompat.getColor(context, R.color.grey_300))
        }
        likeCnt.text = cnt.toString()

        invalidate()
        requestLayout()
    }
}