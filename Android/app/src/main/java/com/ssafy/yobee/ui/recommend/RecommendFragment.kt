package com.ssafy.yobee.ui.recommend

import androidx.appcompat.widget.Toolbar
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentRecommendBinding
import com.ssafy.yobee.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "RecommendFragment_요비"

@AndroidEntryPoint
class RecommendFragment : BaseFragment<FragmentRecommendBinding>(R.layout.fragment_recommend) {
    override fun initView() {
        binding.recommendFragment = this
        initToolbar()
    }

    override fun initViewModels() {
    }

    fun onStartButtonClick() {
        navigate(RecommendFragmentDirections.actionRecommendFragmentToRecommendProgressFragment())
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.layoutToolbar.tbToolbar

        // arg : 타이틀, 백버튼 유무, 백버튼 눌렀을 때 수행할 동작
        toolbar.initToolbar("메뉴 월드컵", true) {
            navigate(RecommendFragmentDirections.actionRecommendFragmentPop())
        }
    }
}