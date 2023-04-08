package com.ssafy.yobee.ui.mypage

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentReviewContentBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.common.CommonDialog
import com.ssafy.yobee.ui.cook.model.GetReviewModel
import com.ssafy.yobee.util.extension.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ReviewContentFragment_요비"

@AndroidEntryPoint
class ReviewContentFragment :
    BaseFragment<FragmentReviewContentBinding>(R.layout.fragment_review_content) {

    private val reviewContentViewModel: ReviewContentViewModel by viewModels()
    private val args: ReviewContentFragmentArgs by navArgs()

    override fun initView() {
        initToolbar()
        initTypes()
        initListener()
        initButtonListener()
        reviewContentViewModel.getReview(args.reviewId)
    }

    override fun initViewModels() {
        reviewContentViewModel.apply {
            getReviewLiveData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Success -> {
                        initContents(response.value!!)
                    }
                    is ViewState.Error -> {
                        Log.d(TAG, "레시피 리뷰 - 상세 에러 : ${response.message}")
                        navigate(ReviewContentFragmentDirections.actionReviewContentFragmentToErrorFragment())
                    }
                    is ViewState.Loading -> {
                        Log.d(TAG, "레시피 리뷰 - 상세 로딩 : ${response.message}")
                    }
                }
            }

            updateReviewLiveData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Success -> {
                        requireContext().showToast("작성되었습니다.")
                        binding.etReviewContent.clearFocus()
                        setEditModeView()
                    }
                    is ViewState.Error -> {
                        Log.d(TAG, "레시피 리뷰 - 수정 에러 : ${response.message}")
                        navigate(ReviewContentFragmentDirections.actionReviewContentFragmentToErrorFragment())
                    }
                    is ViewState.Loading -> {
                        Log.d(TAG, "레시피 리뷰 - 수정 로딩 : ${response.message}")
                    }
                }
            }

            deleteReviewLiveData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Success -> {
                        requireContext().showToast("삭제되었습니다.")
                        navigate(ReviewContentFragmentDirections.actionReviewContentFragmentPop())
                    }
                    is ViewState.Error -> {
                        Log.d(TAG, "레시피 리뷰 - 삭제 에러 : ${response.message}")
                        navigate(ReviewContentFragmentDirections.actionReviewContentFragmentToErrorFragment())
                    }
                    is ViewState.Loading -> {
                        Log.d(TAG, "레시피 리뷰 - 삭제 로딩 : ${response.message}")
                    }
                }
            }
        }
    }


    private fun initToolbar() {
        val toolbar: Toolbar = binding.layoutToolbar.tbToolbar
        toolbar.initToolbar("리뷰", true) {
            findNavController().navigate(R.id.action_reviewContentFragment_pop)
        }
    }

    private fun initTypes() {
        if (args.type == 0) {
            setWriteModeView()
        } else {
            setEditModeView()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        binding.apply {
            clReviewContent.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    etReviewContent.clearFocus()
                }
                false
            }

            etReviewContent.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val len = s?.length ?: 0
                    tvReviewContentCnt.text = "$len"

                    if (len < 5) {
                        btnReviewContentDone.isEnabled = false
                        btnReviewContentEdit.isEnabled = false
                    } else {
                        btnReviewContentDone.isEnabled = true
                        btnReviewContentEdit.isEnabled = true
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            etReviewContent.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    etReviewContent.hideKeyboard()
                }
            }
        }
    }

    private fun setWriteModeView() {
        binding.apply {
            btnReviewContentDone.visibility = View.VISIBLE
            llReviewContentCheck.visibility = View.GONE
        }
    }

    private fun setEditModeView() {
        binding.apply {
            btnReviewContentDone.visibility = View.GONE
            llReviewContentCheck.visibility = View.VISIBLE
        }
    }

    private fun initContents(value: GetReviewModel) {
        binding.apply {
            Glide.with(requireContext())
                .load(value.reviewImage)
                .placeholder(R.drawable.shimmer)
                .into(ivReviewContent)

            if (value.content != null) {
                etReviewContent.setText(value.content)
            }
        }
    }

    private fun initButtonListener() {
        binding.apply {
            btnReviewContentDone.setOnClickListener {
                val content = etReviewContent.text.toString()
                reviewContentViewModel.updateReview(args.reviewId.toInt(), content)
            }
            btnReviewContentEdit.setOnClickListener {
                val content = etReviewContent.text.toString()
                reviewContentViewModel.updateReview(args.reviewId.toInt(), content)
            }
            btnReviewContentDelete.setOnClickListener {
                val dialog = CommonDialog(requireContext(), "리뷰를 삭제하시겠습니까?", "삭제") {
                    reviewContentViewModel.deleteReview(args.reviewId.toInt())
                }
                showDialog(dialog, viewLifecycleOwner)
            }
        }
    }
}
