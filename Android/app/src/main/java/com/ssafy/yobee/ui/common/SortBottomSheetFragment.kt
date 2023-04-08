package com.ssafy.yobee.ui.common

import android.util.Log
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentSortBottomSheetBinding
import com.ssafy.yobee.ui.recipe_list.RecipeListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SortBottomSheetFragment :
    BaseBottomSheetDialogFragment<FragmentSortBottomSheetBinding>(R.layout.fragment_sort_bottom_sheet) {
    private val recipeListViewModel by navGraphViewModels<RecipeListViewModel>(R.id.recipeListFragmentAndBottomSheet) {
        defaultViewModelProviderFactory
    }

    override fun initView() {
        binding.bottomSheetFragment = this
    }

    override fun setEvent() {

    }

    fun navigate(directions: NavDirections) {
        findNavController().navigate(directions)
    }

    fun onClickSortByLike() {
        recipeListViewModel.setSortBy(0)
        Log.d("TAG", "onClickSortByLike: ${recipeListViewModel.sortBy.value}")
        navigate(SortBottomSheetFragmentDirections.actionSortBottomSheetFragmentPop())
    }

    fun onClickSortByReView() {
        recipeListViewModel.setSortBy(1)
        Log.d("TAG", "onClickSortByLike: ${recipeListViewModel.sortBy.value}")
        navigate(SortBottomSheetFragmentDirections.actionSortBottomSheetFragmentPop())
    }

    fun onClickSortByDifficulty() {
        recipeListViewModel.setSortBy(2)
        Log.d("TAG", "onClickSortByLike: ${recipeListViewModel.sortBy.value}")
        navigate(SortBottomSheetFragmentDirections.actionSortBottomSheetFragmentPop())
    }
}