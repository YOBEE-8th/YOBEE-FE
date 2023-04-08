package com.ssafy.yobee.ui.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import com.ssafy.yobee.databinding.CommonDialogBinding
import kotlin.system.exitProcess

class CommonDialog(
    context: Context,
    private val content: String,
    private val customBtn: String,
    private var onCustomListener: (() -> Unit)? = null,
) : Dialog(context) {

    private val binding: CommonDialogBinding = CommonDialogBinding.inflate(layoutInflater)

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
        binding.run {
            tvDialogContent.text = content
            btnDialogCustom.text = customBtn
            btnDialogCustom.setOnClickListener {
                onCustomListener?.invoke()
                dismiss()
            }
            btnDialogCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun show() {
        if (!this.isShowing) super.show()
    }
}