package com.ssafy.yobee.ui.cook.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets

import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.gun0912.tedpermission.PermissionListener
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentAnalyzeImageBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.cook.model.CookProgressViewModel
import com.ssafy.yobee.util.common.CameraUtils
import com.ssafy.yobee.util.common.GalleryUtils
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

private const val TAG = "AnalyzeImageFragment_요비"

@AndroidEntryPoint
class AnalyzeImageFragment :
    BaseFragment<FragmentAnalyzeImageBinding>(R.layout.fragment_analyze_image) {
    private val cookProgressViewModel by navGraphViewModels<CookProgressViewModel>(R.id.cookProgressGraph) {
        defaultViewModelProviderFactory
    }
    lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    lateinit var file: File
    lateinit var bitmap: Bitmap

    private val availableLabel = arrayListOf(
        "Bento",
        "Fast food",
        "Eating",
        "Cuisine",
        "Juice",
        "Menu",
        "Food",
        "Fruit",
        "Bread",
        "Vegetable",
        "Supper",
        "Lunch",
        "Meal"
    )
    private val foodLabel = arrayListOf(
        "Cheeseburger",
        "Hot dog",
        "Cookie",
        "Cola",
        "Gelato",
        "Coffee",
        "Pho",
        "Pizza",
        "Sushi",
        "Wine",
        "Cappuccino",
        "Cake",
        "Alcohol",
        "Pie",
    )

    override fun onResume() {
        super.onResume()
        initImageView(Uri.parse(cookProgressViewModel.uri))
        file = CameraUtils.createImageFile(requireContext())
        bitmap =
            CameraUtils.getBitmapFromUri(requireContext(), Uri.parse(cookProgressViewModel.uri))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.show(WindowInsets.Type.statusBars());
        } else {
            requireActivity().window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initView() {
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        initCameraLauncher()
        initToolbar()
        initButtons()
    }

    override fun initViewModels() {
        initCookProgressViewModel()
    }

    private fun initCookProgressViewModel() {
        cookProgressViewModel.createReviewLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Success -> {
                    dismissLoadingDialog()
                    cookProgressViewModel.setReviewId(response.value!!.reviewId)
                    navigate(AnalyzeImageFragmentDirections.actionAnalyzeImageFragmentToAnalyzeSuccessFragment())
                }
                is ViewState.Error -> {
                    dismissLoadingDialog()
                    Log.d(TAG, "리뷰 작성(이미지만) 에러 : ${response.message}")
                }
                is ViewState.Loading -> {
                    Log.d(TAG, "리뷰 작성(이미지만) 로딩")
                }
            }
        }
    }

    private fun initCameraLauncher() {
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    bitmap = CameraUtils.getBitmapFromFile(requireContext(), file)
                    cookProgressViewModel.setUri(
                        CameraUtils.getUriFromBitmap(
                            requireContext(),
                            bitmap
                        ).toString()
                    )
                    initImageView(Uri.parse(cookProgressViewModel.uri))
                }
            }
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.layoutToolbar.tbToolbar
        toolbar.initToolbar("요리하기", false) {}
    }

    private fun initImageView(uri: Uri) {
        binding.apply {
            Glide.with(requireContext())
                .load(uri)
                .placeholder(R.drawable.shimmer)
                .into(ivImage)
        }
    }

    private fun initButtons() {
        binding.apply {
            btnAnalyzeImageAnalyze.setOnClickListener {
                showLoadingDialog()
                analyzePicture(bitmap)
            }
            btnBtnAnalyzeImageRetry.setOnClickListener {
                requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                CameraUtils.launchCamera(requireContext(), cameraLauncher, file)
            }
        }
    }

    private fun analyzePicture(bitmap: Bitmap) {
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromBitmap(bitmap, 0)

        labeler.process(image)
            .addOnSuccessListener { labels ->
                var isSuccess = false
                for (label in labels) {
                    if (label.text in availableLabel) {
                        if (label.confidence >= 0.65) {
                            isSuccess = true
                            break
                        }
                    }
                    if (label.text in foodLabel) {
                        if (label.confidence >= 0.65) {
                            isSuccess = true
                            break
                        }
                    }
                }
                moveToNextPage(isSuccess)
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "analyzePicture: ${e.printStackTrace()}")
                dismissLoadingDialog()
                navigate(AnalyzeImageFragmentDirections.actionAnalyzeImageFragmentToErrorFragment())
            }
    }

    private fun moveToNextPage(isSuccess: Boolean) {
        if (isSuccess) {
            GalleryUtils.getPermission(object : PermissionListener {
                override fun onPermissionGranted() {
                    val file = File(
                        GalleryUtils.changeAbsolutelyPath(
                            Uri.parse(cookProgressViewModel.uri),
                            requireContext()
                        )
                    )

                    val requestFile = file.asRequestBody("image/ *".toMediaTypeOrNull())
                    val body =
                        MultipartBody.Part.createFormData("reviewImage", file.name, requestFile)
                    cookProgressViewModel.createReview(body, cookProgressViewModel.recipeId, "")
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    dismissLoadingDialog()
                    Toast.makeText(context, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            dismissLoadingDialog()
            navigate(AnalyzeImageFragmentDirections.actionAnalyzeImageFragmentToAnalyzeFailFragment())
        }
    }
}