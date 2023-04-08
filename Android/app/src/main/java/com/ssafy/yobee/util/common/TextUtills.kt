package com.ssafy.yobee.util.common

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan

object TextUtills {
    //textview 특정 text 컬러나 스타일 바꾸기
    fun setSpannableText(text: String, content: String): SpannableString {
        val spannableString = SpannableString(content)
        val start: Int = content.indexOf(text)
        val end = start + text.length

        spannableString.apply {
            setSpan(
                ForegroundColorSpan(Color.parseColor("#FF6347")),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }
}