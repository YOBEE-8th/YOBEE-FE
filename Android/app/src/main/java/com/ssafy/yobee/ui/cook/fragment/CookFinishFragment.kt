package com.ssafy.yobee.ui.cook.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.navGraphViewModels
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentCookFinishBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.cook.model.CookProgressViewModel
import com.ssafy.yobee.util.common.CameraUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "CookFinishFragment_요비"

@AndroidEntryPoint
class CookFinishFragment : BaseFragment<FragmentCookFinishBinding>(R.layout.fragment_cook_finish) {
    private val cookProgressViewModel by navGraphViewModels<CookProgressViewModel>(R.id.cookProgressGraph) {
        defaultViewModelProviderFactory
    }
    lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    lateinit var file: File
    lateinit var uri: String
    lateinit var bitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initView() {
        file = CameraUtils.createImageFile(requireContext())
        initCameraLauncher()
        initButtons()
    }

    override fun initViewModels() {
    }

    private fun initCameraLauncher() {
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    CoroutineScope(Dispatchers.Main).launch {
                        showLoadingDialog()
                        withContext(Dispatchers.IO) {
                            bitmap = CameraUtils.getBitmapFromFile(requireContext(), file)
                            uri = CameraUtils.getUriFromBitmap(requireContext(), bitmap).toString()
                            cookProgressViewModel.setUri(uri)
                        }
                        withContext(Dispatchers.Main) {
                            dismissLoadingDialog()
                            navigate(CookProgressFragmentDirections.actionCookProgressFragmentToAnalyzeImageFragment())
                        }
                    }
                }
            }
    }

    private fun initButtons() {
        binding.apply {
            btnCookFinishPhoto.setOnClickListener {
                CameraUtils.launchCamera(requireContext(), cameraLauncher, file)
            }
            btnCookFinishHome.setOnClickListener {
                navigate(CookProgressFragmentDirections.actionCookProgressFragmentToHomeFragment())
            }
        }
    }
}