package com.ssafy.yobee.ui.recommend

import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentRecommendProgressBinding
import com.ssafy.yobee.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendProgressFragment :
    BaseFragment<FragmentRecommendProgressBinding>(R.layout.fragment_recommend_progress) {
    private val recommendProgressViewModel by viewModels<RecommendProgressViewModel>()
    override fun initView() {
        initToolbar()
    }

    override fun initViewModels() {
        recommendProgressViewModel.apply {
            getRecommendList()
            recommendList.observe(viewLifecycleOwner) {
                when (it) {
                    is ViewState.Error -> {
                        navigate(RecommendProgressFragmentDirections.actionRecommendProgressFragmentToErrorFragment())
                    }
                    is ViewState.Loading -> {}
                    is ViewState.Success -> {
                        Log.d("TAG", "initViewModels: success ${it.value}")
                        binding.recommendProgressViewModel = recommendProgressViewModel
                        setUpsideMenu()
                        setDownsideMenu()
                    }
                }

            }

            selectedMenu.observe(viewLifecycleOwner) {
                SelectedMenuDialog(requireContext(), it.image).show()
                binding.recommendProgressViewModel = recommendProgressViewModel
            }

            upsideMenu.observe(viewLifecycleOwner) {
                binding.upsideMenu = it
            }
            downsideMenu.observe(viewLifecycleOwner) {
                binding.downsideMenu = it
            }
            gameEnd.observe(viewLifecycleOwner) {
                if (it) {
                    val action =
                        RecommendProgressFragmentDirections.actionRecommendProgressFragmentToRecommendFinishFragment(
                            recipeId = selectedMenu.value!!.recipeId,
                            recipeTitle = selectedMenu.value!!.title,
                            recipeImage = selectedMenu.value!!.image
                        )
                    navigate(action)
                }
            }
        }
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.layoutToolbar.tbToolbar

        // arg : 타이틀, 백버튼 유무, 백버튼 눌렀을 때 수행할 동작
        toolbar.initToolbar("메뉴 월드컵", true) {
            navigate(RecommendProgressFragmentDirections.actionRecommendProgressFragmentPop())
        }
    }
}