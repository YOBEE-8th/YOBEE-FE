package com.ssafy.yobee.ui.recipe

import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentIngredientsListBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.recipe.adapter.IngredientsListAdapter
import com.ssafy.yobee.ui.recipe.model.IngredientsModel
import com.ssafy.yobee.util.common.ViewUtils
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "IngredientsListFragment_요비"

@AndroidEntryPoint
class IngredientsListFragment :
    BaseFragment<FragmentIngredientsListBinding>(R.layout.fragment_ingredients_list) {

    private val recipeViewModel: RecipeViewModel by navGraphViewModels(R.id.recipeDetailGraph) {
        defaultViewModelProviderFactory
    }
    private lateinit var ingredientsListAdapter: IngredientsListAdapter
    private lateinit var ingredientsModel: IngredientsModel


    var height = -1

    override fun initView() {
    }

    override fun initViewModels() {
        initRecipeViewModel()
    }

    private fun initRecipeViewModel() {
        recipeViewModel.ingredientLiveData.observe(this) { response ->
            when (response) {
                is ViewState.Success -> {
                    dismissLoadingDialog()
                    ingredientsModel = response.value!!
                    initRecyclerView()
                }
                is ViewState.Error -> {
                    dismissLoadingDialog()
                    Log.d(TAG, "레시피 재료 에러 : ${response.message}")
                    if (findNavController().currentDestination?.id == R.id.recipeDetailFragment) {
                        navigate(RecipeDetailFragmentDirections.actionRecipeDetailFragmentToErrorFragment())
                    }
                }
                is ViewState.Loading -> {
                }
            }
        }
    }

    private fun initRecyclerView() {
        ingredientsListAdapter =
            IngredientsListAdapter(requireContext(), ingredientsModel.ingredients).apply {
                setItemClickListener(object : IngredientsListAdapter.ItemClickListener {
                    override fun onClick(view: View, position: Int) {
                    }
                })
            }

        binding.apply {
            rvIngredients.adapter = ingredientsListAdapter

            rvIngredients.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                recipeViewModel.setIngredientsHeight(
                    ViewUtils.getRecyclerViewItemHeight(
                        rvIngredients,
                        ingredientsListAdapter.itemCount
                    ) + ViewUtils.dpToPx(requireContext(), 20f)
                )
            }
        }
    }
}