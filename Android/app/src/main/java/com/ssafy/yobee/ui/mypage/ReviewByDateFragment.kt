package com.ssafy.yobee.ui.mypage

import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentReviewByDateBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.mypage.adapter.ReviewByDateListAdapter
import com.ssafy.yobee.ui.mypage.model.ReviewByDateModel
import com.ssafy.yobee.util.common.ViewUtils
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ReviewByDateFragment_요비"

@AndroidEntryPoint
class ReviewByDateFragment :
    BaseFragment<FragmentReviewByDateBinding>(R.layout.fragment_review_by_date) {

    private val reviewByDateViewModel: ReviewByDateViewModel by viewModels()
    private lateinit var reviewByDateListAdapter: ReviewByDateListAdapter
    private val args: ReviewByDateFragmentArgs by navArgs()
    private var recyclerViewState: Parcelable? = null

    override fun onResume() {
        super.onResume()

        if (recyclerViewState != null) {
            ViewUtils.setSavedRecyclerViewState(recyclerViewState, binding.rvReviewByDate)
        }
    }

    override fun initView() {
        initToolbar()
        reviewByDateViewModel.getReviewByDate(args.recipeId)
    }

    override fun initViewModels() {
        reviewByDateViewModel.reviewByDateLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Success -> {
                    dismissLoadingDialog()
                    initRecyclerView(response.value!!)
                }
                is ViewState.Error -> {
                    dismissLoadingDialog()
                    Log.d(TAG, "레시피 리뷰 - 날짜별 에러 : ${response.message}")
                    navigate(ReviewByDateFragmentDirections.actionReviewByDateFragmentToErrorFragment())
                }
                is ViewState.Loading -> {
                    showLoadingDialog()
                    Log.d(TAG, "레시피 리뷰 - 날짜별 로딩")
                }
            }
        }
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.layoutToolbar.tbToolbar
        toolbar.initToolbar("리뷰", true) {
            findNavController().navigate(R.id.action_reviewByDateFragment_pop)
        }
    }

    private fun initRecyclerView(value: List<ReviewByDateModel>) {
        reviewByDateListAdapter = ReviewByDateListAdapter(requireContext(), value).apply {
            setItemClickListener(object : ReviewByDateListAdapter.ItemClickListener {
                override fun onWriteBtnClick(view: View, position: Int, reviewId: Long) {
                    recyclerViewState = ViewUtils.saveRecyclerViewState(binding.rvReviewByDate)
                    navigate(
                        ReviewByDateFragmentDirections.actionReviewByDateFragmentToReviewContentFragment(
                            0,
                            reviewId
                        )
                    )
                }

                override fun onCheckBtnClick(view: View, position: Int, reviewId: Long) {
                    recyclerViewState = ViewUtils.saveRecyclerViewState(binding.rvReviewByDate)
                    navigate(
                        ReviewByDateFragmentDirections.actionReviewByDateFragmentToReviewContentFragment(
                            1,
                            reviewId
                        )
                    )
                }
            })
        }

        binding.rvReviewByDate.adapter = reviewByDateListAdapter
    }
}