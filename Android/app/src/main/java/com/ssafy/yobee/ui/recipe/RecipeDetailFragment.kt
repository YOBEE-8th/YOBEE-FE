package com.ssafy.yobee.ui.recipe

import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.ChipHashtagBinding
import com.ssafy.yobee.databinding.FragmentRecipeDetailBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.recipe.adapter.RecipeDetailViewPagerAdapter
import com.ssafy.yobee.ui.recipe.model.RecipeDetailModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "RecipeDetailFragment_요비"

@AndroidEntryPoint
class RecipeDetailFragment :
    BaseFragment<FragmentRecipeDetailBinding>(R.layout.fragment_recipe_detail) {

    private val recipeViewModel: RecipeViewModel by navGraphViewModels(R.id.recipeDetailGraph) {
        defaultViewModelProviderFactory
    }
    private val args: RecipeDetailFragmentArgs by navArgs()

    override fun initView() {
        recipeViewModel.setRecipeId(args.recipeId)
        initToolbar()
        recipeViewModel.getRecipeDetail(args.recipeId)
        initStartButton()
        initLikeToggle()
    }

    override fun initViewModels() {
        initRecipeViewModel()
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.layoutToolbar.tbToolbar
        toolbar.initToolbar("레시피 소개", true) {
            navigate(RecipeDetailFragmentDirections.actionRecipeDetailFragmentPop())
        }
    }

    private fun initViewPager(reviewCnt: Long) {
        val recipeDetailViewPagerAdapter = RecipeDetailViewPagerAdapter(this)
        val tabTitle = listOf("재료", "관련 영상", "리뷰 ($reviewCnt)")

        binding.apply {
            tlRecipeDetailTabs.addOnTabSelectedListener(object :
                TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    Log.d(TAG, "onTabSelected: ${tab?.position}")
                    when (tab?.position) {
                        0 -> {
                            recipeViewModel.getRecipeIngredient(args.recipeId)
                            setHeight(recipeViewModel.ingredientsHeight.value ?: 10000)
                        }
                        1 -> {
                            recipeViewModel.getVideos(
                                "snippet",
                                10,
                                args.recipeTitle,
                                getString(R.string.youtube_api_key_suyong)
                            )
                            setHeight(recipeViewModel.youtubeHeight.value ?: 10000)
                        }
                        2 -> {
                            recipeViewModel.getRecipeReview(args.recipeId)
                            setHeight(recipeViewModel.reviewsHeight.value ?: 10000)
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
            vpRecipeDetailInfo.isUserInputEnabled = false
            vpRecipeDetailInfo.apply {
                adapter = recipeDetailViewPagerAdapter
                offscreenPageLimit = 3
            }

            TabLayoutMediator(tlRecipeDetailTabs, vpRecipeDetailInfo) { tab, position ->
                tab.text = tabTitle[position]
            }.attach()
        }
    }

    fun setHeight(height: Int) = lifecycleScope.launch(Dispatchers.Main) {
        binding.vpRecipeDetailInfo.apply {
            layoutParams.height = height
            requestLayout()
        }
    }

    private fun initStartButton() {
        binding.btnRecipeDetailStart.setOnClickListener {
            navigate(
                RecipeDetailFragmentDirections.actionRecipeDetailFragmentToCookProgressGraph(
                    recipeViewModel.recipeTitle.value!!,
                    recipeViewModel.recipeImg.value!!,
                    args.recipeId
                )
            )
        }
    }

    private fun initLikeToggle() {
        binding.ctvRecipeDetailLike.setOnClickListener {
            recipeViewModel.addLikeRecipe(args.recipeId)
        }
    }

    private fun initRecipeViewModel() {
        recipeViewModel.apply {
            ingredientsHeight.observe(viewLifecycleOwner) {
                setHeight(it)
            }
            youtubeHeight.observe(viewLifecycleOwner) {
                setHeight(it)
            }
            reviewsHeight.observe(viewLifecycleOwner) {
                setHeight(it)
            }


            recipeDetailLiveData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Success -> {
                        dismissLoadingDialog()
                        initViewPager(response.value!!.reviewCnt)
                        recipeViewModel.setRecipeTitle(response.value!!.title)
                        recipeViewModel.setRecipeImage(response.value!!.image)
                        initRecipeDetailView(response.value!!)
                    }
                    is ViewState.Error -> {
                        dismissLoadingDialog()
                        Log.d(TAG, "레시피 소개 에러 : ${response.message}")
                        if (findNavController().currentDestination?.id == R.id.recipeDetailFragment) {
                            navigate(RecipeDetailFragmentDirections.actionRecipeDetailFragmentToErrorFragment())
                        }
                    }
                    is ViewState.Loading -> {
                    }
                }
            }
            addLikeRecipeLiveData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Success -> {
                        recipeViewModel.getRecipeDetail(args.recipeId)
                    }
                    is ViewState.Error -> {
                        Log.d(TAG, "레시피 좋아요 에러 : ${response.message}")
                    }
                    is ViewState.Loading -> {
                    }
                }
            }
        }
    }

    private fun initRecipeDetailView(recipeDetailModel: RecipeDetailModel) {
        binding.apply {
            tvRecipeDetailFood.text = recipeDetailModel.title
            Glide.with(requireContext())
                .load(recipeDetailModel.image)
                .placeholder(R.drawable.shimmer)
                .into(ivRecipeDetailFood)
            tvRecipeDetailTime.text = "${recipeDetailModel.time}분"
            when (recipeDetailModel.level) {
                1 -> ivRecipeDetailLevel.setImageResource(R.drawable.ic_level_1)
                2 -> ivRecipeDetailLevel.setImageResource(R.drawable.ic_level_2)
                3 -> ivRecipeDetailLevel.setImageResource(R.drawable.ic_level_3)
            }
            tvRecipeDetailServings.text = "${recipeDetailModel.servings}인분"
            cvRecipeDetailAi.visibility = if (recipeDetailModel.isAI) {
                View.VISIBLE
            } else {
                View.GONE
            }
            tvRecipeDetailLikeCnt.text = recipeDetailModel.likeCnt.toString()
            ctvRecipeDetailLike.isChecked = recipeDetailModel.isLike
            createChipGroup(recipeDetailModel.hashtag)
        }
    }

    private fun createChipGroup(hashtag: List<String>) {
        binding.cgRecipeDetailHashtag.apply {
            removeAllViews()
            hashtag.forEach { hashTag ->
                addView(createChip(hashTag))
            }
        }
    }

    private fun createChip(tag: String): Chip {
        val chip = ChipHashtagBinding.inflate(layoutInflater).root as Chip
        chip.text = "#$tag"
        return chip
    }
}
