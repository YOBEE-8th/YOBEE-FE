package com.ssafy.yobee.ui.mypage_like_recipe

import android.os.Parcelable
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import com.ssafy.domain.model.recipe.RecipeDomainModel
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentLikeRecipeBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.recipe_list.adapter.RecipeListAdapter
import com.ssafy.yobee.util.common.ViewUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LikeRecipeFragment : BaseFragment<FragmentLikeRecipeBinding>(R.layout.fragment_like_recipe) {
    private lateinit var likeRecipeListAdapter: RecipeListAdapter
    private val likeRecipeListViewModel: LikeRecipeViewModel by viewModels()
    private var recyclerViewState: Parcelable? = null
    override fun initView() {
        initAdapter()
        initToolbar()
    }

    private fun initAdapter() {
        likeRecipeListAdapter = RecipeListAdapter().apply {
            this.addRecipeItemClickListener { recipeId, recipeTitle ->
                recyclerViewState = ViewUtils.saveRecyclerViewState(binding.rvRecipeList)
                navigate(
                    LikeRecipeFragmentDirections.actionLikeRecipeFragmentToRecipeDetailGraph(
                        recipeId,
                        recipeTitle
                    )
                )
            }
            this.addLikeRecipeButtonClickListener {
                likeRecipeListViewModel.changeRecipeLikeStatus(it)
            }
        }
        binding.rvRecipeList.adapter = likeRecipeListAdapter
    }

    override fun initViewModels() {
        likeRecipeListViewModel.apply {
            getLikeRecipeList()
            recipeList.observe(viewLifecycleOwner) {
                when (it) {
                    is ViewState.Error -> {
                        dismissLoadingDialog()
                        navigate(LikeRecipeFragmentDirections.actionLikeRecipeFragmentToErrorFragment())
                    }
                    is ViewState.Loading -> {
                        showLoadingDialog()
                    }
                    is ViewState.Success -> {
                        dismissLoadingDialog()
                        if (it.value!!.isEmpty()) {
                            setNoItemView(true, it.value!!)
                        } else {
                            setNoItemView(false, it.value!!)
                        }
                    }
                }
            }
        }

    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.layoutToolbar.tbToolbar
        // arg : 타이틀, 백버튼 유무, 백버튼 눌렀을 때 수행할 동작
        toolbar.initToolbar("즐겨찾기", true) {
            navigate(LikeRecipeFragmentDirections.actionLikeRecipeFragmentPop())
        }
    }

    private fun setNoItemView(isEmpty: Boolean, result: List<RecipeDomainModel>) {
        binding.apply {
            if (isEmpty) {
                layoutNoItem.clNoItem.visibility = View.VISIBLE
                layoutNoItem.tvNoItem.text = getString(R.string.title_no_item_my_interest)
                rvRecipeList.visibility = View.GONE
            } else {
                layoutNoItem.clNoItem.visibility = View.GONE
                rvRecipeList.visibility = View.VISIBLE
                likeRecipeListAdapter.submitList(result)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (recyclerViewState != null) {
            ViewUtils.setSavedRecyclerViewState(recyclerViewState, binding.rvRecipeList)
        }
    }
}