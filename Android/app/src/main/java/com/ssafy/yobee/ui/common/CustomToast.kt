package com.ssafy.yobee.ui.common

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.ToastCustomBinding

object CustomToast {
    fun makeText(context: Context, message: String): Toast? {
        val inflater = LayoutInflater.from(context)
        val binding: ToastCustomBinding =
            DataBindingUtil.inflate(inflater, R.layout.toast_custom, null, false)

        binding.tvToast.text = message

        return Toast(context).apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 30.toPx())
            duration = Toast.LENGTH_SHORT
            view = binding.root
        }
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}