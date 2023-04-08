package com.ssafy.yobee.ui.mypage

import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentReviewByRecipeBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.mypage.adapter.ReviewByRecipeListAdapter
import com.ssafy.yobee.ui.mypage.model.ReviewByRecipeModel
import com.ssafy.yobee.util.common.ViewUtils
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ReviewByRecipeFragment_요비"

@AndroidEntryPoint
class ReviewByRecipeFragment :
    BaseFragment<FragmentReviewByRecipeBinding>(R.layout.fragment_review_by_recipe) {

    private val reviewByRecipeViewModel: ReviewByRecipeViewModel by viewModels()
    private lateinit var reviewByRecipeListAdapter: ReviewByRecipeListAdapter
    private var recyclerViewState: Parcelable? = null

    override fun onResume() {
        super.onResume()

        if (recyclerViewState != null) {
            ViewUtils.setSavedRecyclerViewState(recyclerViewState, binding.rvReviewByRecipe)
        }
    }

    override fun initView() {
        initToolbar()
        reviewByRecipeViewModel.getReviewByRecipe()
    }

    override fun initViewModels() {
        reviewByRecipeViewModel.reviewByRecipeLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Success -> {
                    dismissLoadingDialog()
                    if (response.value!!.isEmpty()) {
                        setNoItemView(true, response.value!!)
                    } else {
                        setNoItemView(false, response.value!!)
                    }
                }
                is ViewState.Error -> {
                    dismissLoadingDialog()
                    Log.d(TAG, "레시피 리뷰 - 레시피별 에러 : ${response.message}")
                    navigate(ReviewByRecipeFragmentDirections.actionReviewByRecipeFragmentToErrorFragment())
                }
                is ViewState.Loading -> {
                    showLoadingDialog()
                    Log.d(TAG, "레시피 리뷰 - 레시피별 로딩")
                }
            }
        }
    }

    private fun setNoItemView(isEmpty: Boolean, result: List<ReviewByRecipeModel>) {
        binding.apply {
            if (isEmpty) {
                layoutNoItem.clNoItem.visibility = View.VISIBLE
                layoutNoItem.tvNoItem.text = getString(R.string.title_no_item_my_review)
                rvReviewByRecipe.visibility = View.GONE
            } else {
                layoutNoItem.clNoItem.visibility = View.GONE
                rvReviewByRecipe.visibility = View.VISIBLE
                initRecyclerView(result)
            }
        }
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.layoutToolbar.tbToolbar
        toolbar.initToolbar("리뷰", true) {
            findNavController().navigate(R.id.action_reviewByRecipeFragment_pop)
        }
    }

    private fun initRecyclerView(value: List<ReviewByRecipeModel>) {
        reviewByRecipeListAdapter = ReviewByRecipeListAdapter(requireContext(), value).apply {
            setItemClickListener(object : ReviewByRecipeListAdapter.ItemClickListener {
                override fun onClick(view: View, position: Int, recipeId: Long) {
                    recyclerViewState = ViewUtils.saveRecyclerViewState(binding.rvReviewByRecipe)
                    navigate(
                        ReviewByRecipeFragmentDirections.actionReviewByRecipeFragmentToReviewByDateFragment(
                            recipeId
                        )
                    )
                }
            })
        }

        binding.rvReviewByRecipe.adapter = reviewByRecipeListAdapter
    }
}