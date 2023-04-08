package com.ssafy.yobee.ui.recipe

import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentReviewListBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.common.CommonDialog
import com.ssafy.yobee.ui.recipe.adapter.ReviewListAdapter
import com.ssafy.yobee.ui.recipe.model.ReviewModel
import com.ssafy.yobee.util.common.ViewUtils
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ReviewListFragment_요비"

@AndroidEntryPoint
class ReviewListFragment : BaseFragment<FragmentReviewListBinding>(R.layout.fragment_review_list) {

    private val recipeViewModel: RecipeViewModel by navGraphViewModels(R.id.recipeDetailGraph) {
        defaultViewModelProviderFactory
    }
    private lateinit var reviewListAdapter: ReviewListAdapter
    private lateinit var reviewModel: List<ReviewModel>
    var height = -1

    override fun initView() {
    }

    override fun initViewModels() {
        initRecipeViewModel()
    }

    private fun initRecipeViewModel() {
        recipeViewModel.apply {
            recipeReviewLiveData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Success -> {
                        reviewModel = response.value!!
                        if (reviewModel.isEmpty()) {
                            setNoItemView(true)
                        } else {
                            setNoItemView(false)
                        }
                    }
                    is ViewState.Error -> {
                        Log.d(TAG, "레시피 리뷰 에러 : ${response.message}")
                        if (findNavController().currentDestination?.id == R.id.recipeDetailFragment) {
                            navigate(RecipeDetailFragmentDirections.actionRecipeDetailFragmentToErrorFragment())
                        }
                    }
                    is ViewState.Loading -> {
                    }
                }
            }

            updateReviewLikeLiveData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Success -> {
                        dismissLoadingDialog()
                        getRecipeReview(recipeViewModel.recipeId.value!!)
                    }
                    is ViewState.Error -> {
                        dismissLoadingDialog()
                        Log.d(TAG, "레시피 리뷰 좋아요 에러 : ${response.message}")
                        if (findNavController().currentDestination?.id == R.id.recipeDetailFragment) {
                            navigate(RecipeDetailFragmentDirections.actionRecipeDetailFragmentToErrorFragment())
                        }
                    }
                    is ViewState.Loading -> {
                    }
                }
            }

            deleteReviewLiveData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Success -> {
                        getRecipeReview(recipeViewModel.recipeId.value!!)
                    }
                    is ViewState.Error -> {
                        Log.d(TAG, "레시피 리뷰 삭제 에러 : ${response.message}")
                        if (findNavController().currentDestination?.id == R.id.recipeDetailFragment) {
                            navigate(RecipeDetailFragmentDirections.actionRecipeDetailFragmentToErrorFragment())
                        }
                    }
                    is ViewState.Loading -> {
                    }
                }
            }
        }
    }

    private fun setNoItemView(isEmpty: Boolean) {
        binding.apply {
            if (isEmpty) {
                layoutNoItem.clNoItem.visibility = View.VISIBLE
                layoutNoItem.tvNoItem.text = getString(R.string.title_no_item_recipe_review)
                rvReviews.visibility = View.GONE
                recipeViewModel.setReviewsHeight(ViewUtils.dpToPx(requireContext(), 400f))
            } else {
                layoutNoItem.clNoItem.visibility = View.GONE
                rvReviews.visibility = View.VISIBLE
                initRecyclerView()
            }
        }
    }

    private fun initRecyclerView() {
        reviewListAdapter = ReviewListAdapter(requireContext(), reviewModel).apply {
            setItemClickListener(object : ReviewListAdapter.ItemClickListener {
                override fun onLikeClick(view: View, position: Int, reviewId: Int) {
                    recipeViewModel.updateReviewLike(reviewId)
                }

                override fun onDeleteClick(view: View, position: Int, reviewId: Int) {
                    val dialog =
                        CommonDialog(requireContext(), "리뷰를 삭제하시겠습니까?", "삭제") {
                            recipeViewModel.deleteReview(reviewId)
                        }
                    showDialog(dialog, viewLifecycleOwner)
                }
            })
        }

        binding.apply {
            rvReviews.adapter = reviewListAdapter
            rvReviews.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                recipeViewModel.setReviewsHeight(
                    ViewUtils.getRecyclerViewItemHeight(
                        rvReviews,
                        reviewListAdapter.itemCount
                    )
                )
            }
        }
    }
}