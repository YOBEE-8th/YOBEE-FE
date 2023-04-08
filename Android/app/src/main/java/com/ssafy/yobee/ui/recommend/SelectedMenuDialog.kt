package com.ssafy.yobee.ui.recommend

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.ssafy.yobee.databinding.DialogSelectedMenuBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class SelectedMenuDialog(context: Context, val url: String) : Dialog(context) {
    private val binding = DialogSelectedMenuBinding.inflate(layoutInflater)

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        window?.run {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 50))
            attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
            attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        } ?: exitProcess(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.selectedMenuDialog = this
    }

    override fun show() {
        if (!this.isShowing) super.show()
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            dismiss()
        }
    }
}