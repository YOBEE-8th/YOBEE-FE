package com.ssafy.yobee.ui.search

import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentSearchSortBottomSheetBinding
import com.ssafy.yobee.ui.common.BaseBottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchSortBottomSheetFragment :
    BaseBottomSheetDialogFragment<FragmentSearchSortBottomSheetBinding>(R.layout.fragment_search_sort_bottom_sheet) {
    private val recipeListViewModel by navGraphViewModels<RecipeSearchViewModel>(R.id.recipeSearchFragmentAndBottomSheet) {
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
        navigate(SearchSortBottomSheetFragmentDirections.actionSearchSortBottomSheetFragmentPop())
    }

    fun onClickSortByReView() {
        recipeListViewModel.setSortBy(1)
        navigate(SearchSortBottomSheetFragmentDirections.actionSearchSortBottomSheetFragmentPop())
    }

    fun onClickSortByDifficulty() {
        recipeListViewModel.setSortBy(2)
        navigate(SearchSortBottomSheetFragmentDirections.actionSearchSortBottomSheetFragmentPop())
    }
}