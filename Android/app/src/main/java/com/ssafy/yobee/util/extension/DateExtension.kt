package com.ssafy.yobee.util.extension

import java.text.SimpleDateFormat
import java.util.*

fun getDeadLineByEndDate(endDate: Long): Int {
    val currentDay = System.currentTimeMillis()
    val deadLine = (endDate - currentDay).toDouble() / (1000 * 60 * 60 * 24)
    return if (deadLine < 0) -1 else deadLine.toInt()
}

fun getFormattedCurrentTime(): String {
    val formatter = SimpleDateFormat("yyyyMMdd_hhmmss_", Locale.KOREA)
    return formatter.format(Date(System.currentTimeMillis()))
}

fun getReviewDateType(date: Date): String {
    val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
    return formatter.format(date)
}