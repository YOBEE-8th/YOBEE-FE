package com.ssafy.yobee.ui.cook.fragment

import android.graphics.Typeface
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.navGraphViewModels
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentAnalyzeSuccessBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.common.GraphUtils
import com.ssafy.yobee.ui.cook.model.CookProgressViewModel
import com.ssafy.yobee.ui.cook.model.IncresedExpModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AnalyzeSuccessFragment_요비"

@AndroidEntryPoint
class AnalyzeSuccessFragment :
    BaseFragment<FragmentAnalyzeSuccessBinding>(R.layout.fragment_analyze_success) {

    private val cookProgressViewModel by navGraphViewModels<CookProgressViewModel>(R.id.cookProgressGraph) {
        defaultViewModelProviderFactory
    }
    private lateinit var incresedExpModel: IncresedExpModel

    override fun initView() {
        initToolbar()
        initButtons()
        cookProgressViewModel.increaseExp(cookProgressViewModel.recipeId)
    }

    override fun initViewModels() {
        initCookProgressViewModel()
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.layoutToolbar.tbToolbar
        toolbar.initToolbar("요리하기", false) {}
    }

    private fun initButtons() {
        binding.apply {
            btnAnalyzeSuccessHome.setOnClickListener {
                navigate(AnalyzeSuccessFragmentDirections.actionAnalyzeSuccessFragmentToHomeFragment())
            }
            btnAnalyzeSuccessReview.setOnClickListener {
                navigate(
                    AnalyzeSuccessFragmentDirections.actionAnalyzeSuccessFragmentToReviewContentFragment(
                        0,
                        cookProgressViewModel.reviewId.toLong()
                    )
                )
            }
        }
    }

    private fun initCookProgressViewModel() {
        cookProgressViewModel.increaseExpLiveData.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ViewState.Success -> {
                    incresedExpModel = response.value!!
                    initRadarChart()
                    initExpValue()
                }
                is ViewState.Error -> {
                    Log.d(TAG, "경험치 상승 에러 : ${response.message}")
                    navigate(AnalyzeSuccessFragmentDirections.actionAnalyzeSuccessFragmentToErrorFragment())
                }
                is ViewState.Loading -> {
                    Log.d(TAG, "경험치 상승 로딩")
                }
            }
        }
    }

    private fun initExpValue() {
        binding.apply {
            tvAnalyzeSuccessCategory.text = incresedExpModel.upCategory
            tvAnalyzeSuccessNum.text = "${incresedExpModel.upExp}점"
        }
    }

    private fun initRadarChart() {
        val maxVal = if (incresedExpModel.expList.max() <= 0) {
            1f
        } else {
            incresedExpModel.expList.max()
        }

        val radarData = RadarData()
        radarData.addDataSet(GraphUtils.getBackgroundDataSet(requireContext(), maxVal))

        val valueRadarEntry: ArrayList<RadarEntry> = ArrayList()
        incresedExpModel.expList.forEach {
            valueRadarEntry.add(RadarEntry(it))
        }
        radarData.addDataSet(GraphUtils.getChartDataSet(requireContext(), valueRadarEntry))

        val labels = arrayOf("국/찌개", "면", "디저트", "구이/볶음", "반찬")

        binding.rcAnalyzeSuccessExp.apply {
            clear()
            notifyDataSetChanged()
            invalidate()

            setExtraOffsets(28f, 20f, 28f, 0f)

            // 그래프 값 범위 설정
            yAxis.apply {
                axisMinimum = 0f
                axisMaximum = maxVal
                setLabelCount(6, true)
            }

            // 데이터와 라벨 설정
            data = radarData
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                textSize = 11f
                textColor = ContextCompat.getColor(requireContext(), R.color.bittersweet_400)
                typeface = Typeface.DEFAULT_BOLD
            }

            // 차트 안쪽 선 색상 설정
            webColor = ContextCompat.getColor(requireContext(), R.color.grey_300)
            webColorInner = ContextCompat.getColor(requireContext(), R.color.grey_300)
            webLineWidth = 0.5f
            webLineWidthInner = 0.5f

            // 회전, 터치 방지
            isRotationEnabled = false
            setTouchEnabled(false)

            // 필요 없는거 안보이게 설정
            legend.isEnabled = false
            description.isEnabled = false
            yAxis.setDrawLabels(false)
        }
    }
}