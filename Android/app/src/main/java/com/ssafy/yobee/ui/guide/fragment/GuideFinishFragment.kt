package com.ssafy.yobee.ui.guide.fragment

import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentGuideFinishBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.guide.GuideFragmentDirections

class GuideFinishFragment : BaseFragment<FragmentGuideFinishBinding>(R.layout.fragment_guide_finish) {

    override fun initView() {
        initButtons()
    }

    override fun initViewModels() {
    }

    private fun initButtons() {
        binding.apply {
            btnGuideFinishCook.setOnClickListener {
                navigate(GuideFragmentDirections.actionGuideFragmentPop())
            }
        }
    }
}