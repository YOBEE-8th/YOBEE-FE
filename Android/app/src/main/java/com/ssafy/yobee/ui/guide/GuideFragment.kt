package com.ssafy.yobee.ui.guide

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentGuideBinding
import com.ssafy.yobee.ui.common.BaseFragment

class GuideFragment : BaseFragment<FragmentGuideBinding>(R.layout.fragment_guide) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().requestedOrientation =
            if (Build.VERSION.SDK_INT < 9) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        // Activity의 상태 표시줄 숨기기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars());
        } else {
            requireActivity().window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    override fun initView() {
        initViewPager()
    }

    private fun initViewPager() {
        val guideViewPagerAdapter = GuideViewPagerAdapter(this)

        binding.vpGuide.apply {
            adapter = guideViewPagerAdapter
            offscreenPageLimit = 9
        }
    }

    override fun initViewModels() {
    }
}