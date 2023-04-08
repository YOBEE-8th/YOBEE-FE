package com.ssafy.yobee.ui.guide

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ssafy.yobee.ui.guide.fragment.*

class GuideViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments: List<Fragment> = listOf(
        BaseCommandFragment(),
        NoNameCommandFragment(),
        DirectionCommandFragment(),
        QuestionCommandFragment(),
        MicDetectionFragment(),
        FireStrengthFragment(),
        SetTimerFragment(),
        SetVolumeFragment(),
        GuideFinishFragment()
    )

    override fun getItemCount(): Int = 9

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}