package com.ssafy.yobee.ui.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.ssafy.yobee.databinding.FragmentLoadingDialogBinding

class LoadingDialog() : DialogFragment() {

    private lateinit var binding: FragmentLoadingDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoadingDialogBinding.inflate(layoutInflater)

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setDimAmount(0.3f)

        isCancelable = false

        return binding.root
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val fragmentTransaction = manager.beginTransaction()
            fragmentTransaction.add(this, tag)
            fragmentTransaction.commitAllowingStateLoss()
        } catch (ignored: IllegalStateException) {
        }
    }
}