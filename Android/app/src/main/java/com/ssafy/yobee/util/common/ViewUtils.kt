package com.ssafy.yobee.util.common

import android.content.Context
import android.os.Parcelable
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "ViewUtils_요비"

object ViewUtils {
    fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun getRecyclerViewItemHeight(recyclerView: RecyclerView, cnt: Int): Int {
        val layoutManager = recyclerView.layoutManager
        var height = 0

        for (i in 0 until cnt) {
            val view = layoutManager?.findViewByPosition(i) ?: continue
            val params = view.layoutParams as RecyclerView.LayoutParams
            view.measure(
                View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            height += view.measuredHeight + params.topMargin + params.bottomMargin
        }
        return height
    }

    fun saveRecyclerViewState(recyclerView: RecyclerView): Parcelable? {
        return recyclerView.layoutManager!!.onSaveInstanceState()
    }

    fun setSavedRecyclerViewState(recyclerViewState: Parcelable?, recyclerView: RecyclerView) {
        recyclerView.layoutManager!!.onRestoreInstanceState(recyclerViewState)
    }
}