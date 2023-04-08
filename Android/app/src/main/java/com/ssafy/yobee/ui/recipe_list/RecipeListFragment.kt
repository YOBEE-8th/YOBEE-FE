package com.ssafy.yobee.ui.recipe_list

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentRecipeListBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.recipe_list.adapter.RecipeListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeListFragment : BaseFragment<FragmentRecipeListBinding>(R.layout.fragment_recipe_list) {
    private val recipeListViewModel: RecipeListViewModel by navGraphViewModels(R.id.recipeListFragmentAndBottomSheet) {
        defaultViewModelProviderFactory
    }
    private val args by navArgs<RecipeListFragmentArgs>()

    private var recipeListAdapter = RecipeListAdapter().apply {
        addLikeRecipeButtonClickListener { recipeId ->
            recipeListViewModel.changeRecipeLikeStatus((recipeId))
        }
        addRecipeItemClickListener { recipeId, recipeTitle ->
            navigate(
                RecipeListFragmentDirections.actionRecipeListFragmentToRecipeDetailGraph(
                    recipeTitle, recipeId
                )
            )
        }
    }

    override fun initView() {
        initToolbar()
        initAdapter()
        binding.recipeFragment = this
    }

    fun initObservers() {
        with(recipeListViewModel) {
            category.observe(viewLifecycleOwner) {
                getRecipeList()
            }
            sortBy.observe(viewLifecycleOwner) {
                clearDataList()
                initPage()
                getRecipeList()
            }
            order.observe(viewLifecycleOwner) {
                clearDataList()
                initPage()
                getRecipeList()
            }
            recipeList.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Loading -> {
                        showLoadingDialog()
                    }
                    is ViewState.Success -> {
                        binding.recipeListViewModel = recipeListViewModel
                        if (response.message!!.split(" ")[0] == "refresh") {
                            recipeListAdapter.submitList(dataList)
                            recipeListAdapter.notifyItemChanged(response.message!!.split(" ")[1].toInt())
                        }
                        else {
                            if (!response.value!!.isEmpty()) {
                                recipeListViewModel.addDataList(response.value!!)
                                recipeListAdapter.submitList(dataList)
                                recipeListAdapter.notifyDataSetChanged()
                            }
                        }
                        dismissLoadingDialog()
                    }
                    is ViewState.Error -> {
                        navigate(RecipeListFragmentDirections.actionRecipeListFragmentToErrorFragment())
                        dismissLoadingDialog()
                    }
                }
            }
        }
    }

    override fun initViewModels() {
        binding.recipeListViewModel = recipeListViewModel
        initObservers()
    }

    fun initAdapter() {
        binding.rvRecipeList.adapter = recipeListAdapter
        binding.rvRecipeList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!binding.rvRecipeList.canScrollVertically(1)) {
                    // 스크롤이 끝까지 내려갔을 때
                    recipeListViewModel.plusPage()
                    recipeListViewModel.getRecipeList()
                }
            }
        })


    }

    fun onSortButtonClick() {
        navigate(RecipeListFragmentDirections.actionRecipeListFragmentToSortBottomSheetFragment())
    }

    fun onOrderButtonClick() {
        recipeListViewModel.setOrder()
    }

    fun onIsAiCheckBoxClick() {
        recipeListViewModel.setIsAI()
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.layoutToolbar.tbToolbar
        var toolbarTitle = args.category
        when (toolbarTitle) {
            "all" -> toolbarTitle = getString(R.string.content_category_all)
            "soup" -> toolbarTitle = getString(R.string.content_category_soup)
            "side" -> toolbarTitle = getString(R.string.content_category_side)
            "grilled" -> toolbarTitle = getString(R.string.content_category_grilled)
            "noodle" -> toolbarTitle = getString(R.string.content_category_noodle)
            "dessert" -> toolbarTitle = getString(R.string.content_category_dessert)
        }
        recipeListViewModel.setCategory(toolbarTitle)
        // arg : 타이틀, 백버튼 유무, 백버튼 눌렀을 때 수행할 동작
        toolbar.initToolbar(toolbarTitle, true) {
            navigate(RecipeListFragmentDirections.actionRecipeListFragmentPop())
        }
    }
}