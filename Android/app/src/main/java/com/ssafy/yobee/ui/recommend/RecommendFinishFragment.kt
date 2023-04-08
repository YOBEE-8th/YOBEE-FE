package com.ssafy.yobee.ui.recommend

import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.navArgs
import com.ssafy.domain.model.recommend.RecommendDomainModel
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentRecommendFinishBinding
import com.ssafy.yobee.ui.common.BaseFragment


class RecommendFinishFragment :
    BaseFragment<FragmentRecommendFinishBinding>(R.layout.fragment_recommend_finish) {
    private val args: RecommendFinishFragmentArgs by navArgs()
    lateinit var recommendResult: RecommendDomainModel
    override fun initView() {
        initToolbar()
        recommendResult = RecommendDomainModel(
            recipeId = args.recipeId,
            title = args.recipeTitle,
            image = args.recipeImage
        )
        binding.recommendResult = this.recommendResult
        binding.recommendFinishFragment = this
        binding.btnRecommendFinishGoHome.setOnClickListener {
            navigate(RecommendFinishFragmentDirections.actionRecommendFinishFragmentToHomeFragment())
        }
    }

    override fun initViewModels() {
    }

    fun letsCookClick() {
        val action =
            RecommendFinishFragmentDirections.actionRecommendFinishFragmentToRecipeDetailGraph(
                recommendResult.title,
                recommendResult.recipeId
            )
        navigate(action)
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.layoutToolbar.tbToolbar

        // arg : 타이틀, 백버튼 유무, 백버튼 눌렀을 때 수행할 동작
        toolbar.initToolbar("메뉴 월드컵", true) {
            navigate(RecommendProgressFragmentDirections.actionRecommendProgressFragmentPop())
        }
    }
}