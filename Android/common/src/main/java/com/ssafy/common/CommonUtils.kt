package com.ssafy.common

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object CommonUtils {
    @SuppressLint("SimpleDateFormat")
    fun dateToString(date: Date): String {
        val format = SimpleDateFormat("yyyy.MM.dd")
        return format.format(date)
    }
}