package com.ssafy.yobee.ui.cook

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.ssafy.yobee.databinding.DialogAnswerBinding
import kotlin.system.exitProcess

class AnswerDialog(context: Context, private val content: String) : Dialog(context) {
    private val binding = DialogAnswerBinding.inflate(layoutInflater)

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)
        window?.run {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 50))
            attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
            attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT
            setCanceledOnTouchOutside(true)
            setCancelable(true)
        } ?: exitProcess(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.run {
            tvAnswer.text = content
        }
    }

    override fun show() {
        if (!this.isShowing) super.show()
    }
}