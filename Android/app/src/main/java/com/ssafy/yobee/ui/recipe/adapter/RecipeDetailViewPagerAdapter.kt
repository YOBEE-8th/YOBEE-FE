package com.ssafy.yobee.ui.recipe.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ssafy.yobee.ui.recipe.IngredientsListFragment
import com.ssafy.yobee.ui.recipe.ReviewListFragment
import com.ssafy.yobee.ui.recipe.VideoListFragment

private const val TAG = "RecipeDetailViewPagerAd_요비"

class RecipeDetailViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments: List<Fragment> = listOf(
        IngredientsListFragment(),
        VideoListFragment(),
        ReviewListFragment()
    )

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }


}