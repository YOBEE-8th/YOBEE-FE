package com.ssafy.yobee.ui.cook.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.navigation.navGraphViewModels
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentAnalyzeFailBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.cook.model.CookProgressViewModel
import com.ssafy.yobee.util.common.CameraUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AnalyzeFailFragment :
    BaseFragment<FragmentAnalyzeFailBinding>(R.layout.fragment_analyze_fail) {
    private val cookProgressViewModel by navGraphViewModels<CookProgressViewModel>(R.id.cookProgressGraph) {
        defaultViewModelProviderFactory
    }
    lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    lateinit var file: File
    lateinit var uri: String
    lateinit var bitmap: Bitmap

    override fun initView() {
        initToolbar()
        file = CameraUtils.createImageFile(requireContext())
        initCameraLauncher()
        initButtons()
    }

    override fun initViewModels() {
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.layoutToolbar.tbToolbar
        toolbar.initToolbar("요리하기", false) {}
    }

    private fun initCameraLauncher() {
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    bitmap = CameraUtils.getBitmapFromFile(requireContext(), file)
                    uri = CameraUtils.getUriFromBitmap(requireContext(), bitmap).toString()
                    cookProgressViewModel.setUri(uri)
                }
                navigate(AnalyzeFailFragmentDirections.actionAnalyzeFailFragmentPop())
            }
    }

    private fun initButtons() {
        binding.apply {
            btnAnalyzeFailRetry.setOnClickListener {
                CameraUtils.launchCamera(requireContext(), cameraLauncher, file)
            }
            btnAnalyzeFailHome.setOnClickListener {
                navigate(AnalyzeFailFragmentDirections.actionAnalyzeFailFragmentToHomeFragment())
            }
        }
    }
}