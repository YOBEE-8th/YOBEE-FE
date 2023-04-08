package com.ssafy.yobee.ui.common

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ssafy.yobee.R

abstract class BaseBottomSheetDialogFragment<B : ViewBinding>(@LayoutRes private val layoutRes: Int) :
    BottomSheetDialogFragment() {
    private var _binding: B? = null
    val binding get() = _binding ?: throw IllegalStateException("binding fail")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return _binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            setUpRatio(bottomSheet!!)
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setEvent()
    }

    abstract fun initView()
    abstract fun setEvent()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setUpRatio(view: View) {
        val layoutParams = view.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        view.layoutParams = layoutParams
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 240F, resources.displayMetrics
        ).toInt()
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }
}