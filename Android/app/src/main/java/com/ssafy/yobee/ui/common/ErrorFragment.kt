package com.ssafy.yobee.ui.common

import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentErrorBinding

class ErrorFragment : BaseFragment<FragmentErrorBinding>(R.layout.fragment_error) {
    override fun initView() {
        setRefreshLayoutEvent()
    }

    override fun initViewModels() {

    }

    private fun setRefreshLayoutEvent() {
        binding.apply {
            srlRefreshError.setOnRefreshListener {
                navigate(ErrorFragmentDirections.actionErrorFragmentPop())
            }
        }
    }
}