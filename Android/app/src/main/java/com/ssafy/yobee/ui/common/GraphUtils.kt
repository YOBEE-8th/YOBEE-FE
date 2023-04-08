package com.ssafy.yobee.ui.common

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.ssafy.yobee.R

object GraphUtils {
    fun getBackgroundDataSet(context: Context, maxVal: Float): RadarDataSet {
        // 그래프 배경을 위한 데이터
        val bgRadarEntry: ArrayList<RadarEntry> = ArrayList()
        for (i in 0..4) {
            bgRadarEntry.add(RadarEntry(maxVal))
        }

        val bgRadarDataSet = RadarDataSet(bgRadarEntry, "bgRadarEntry")
        bgRadarDataSet.color = ContextCompat.getColor(context, R.color.bittersweet_100)
        bgRadarDataSet.fillColor = ContextCompat.getColor(context, R.color.bittersweet_200)
        bgRadarDataSet.setDrawFilled(true)
        bgRadarDataSet.fillAlpha = 60
        bgRadarDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return ""
            }
        }

        return bgRadarDataSet
    }

    fun getChartDataSet(context: Context, radarEntry: ArrayList<RadarEntry>): RadarDataSet {
        val valueRadarDataSet = RadarDataSet(radarEntry, "bgRadarEntry")
        valueRadarDataSet.color = ContextCompat.getColor(context, R.color.bittersweet_300)
        valueRadarDataSet.fillColor =
            ContextCompat.getColor(context, R.color.bittersweet_400)
        valueRadarDataSet.setDrawFilled(true)
        valueRadarDataSet.fillAlpha = 100
        valueRadarDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return ""
            }
        }

        return valueRadarDataSet
    }
}