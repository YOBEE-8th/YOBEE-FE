package com.ssafy.yobee.ui.cook.fragment

import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import com.gun0912.tedpermission.PermissionListener
import com.ssafy.domain.model.cook.CookProgressStepDomainModel
import com.ssafy.yobee.R
import com.ssafy.yobee.alarm.AlarmFunctions
import com.ssafy.yobee.databinding.FragmentCookProgressStepBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.cook.model.CookProgressStepViewModel
import com.ssafy.yobee.ui.cook.model.CookProgressViewModel
import com.ssafy.yobee.util.common.NotificationUtils
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CookProgressStepFragment(private val cookProgressStepDomainModel: CookProgressStepDomainModel) :
    BaseFragment<FragmentCookProgressStepBinding>(R.layout.fragment_cook_progress_step) {
    private val cookProgressViewModel by navGraphViewModels<CookProgressViewModel>(R.id.cookProgressGraph) { defaultViewModelProviderFactory }
    private val cookProgressStepViewModel by viewModels<CookProgressStepViewModel>()


    override fun initView() {
        binding.apply {
            this.cookProgressStepViewModel = this@CookProgressStepFragment.cookProgressStepViewModel
            ivCookProgressTimer.setOnClickListener {
                onTimerClick()
            }
        }
    }


    override fun initViewModels() {
        with(cookProgressStepViewModel) {
            initData(cookProgressStepDomainModel)
        }
        cookProgressViewModel.sttStatus.observe(viewLifecycleOwner) {
            binding.cookProgressViewModel = cookProgressViewModel
        }
    }


    fun onTimerClick() {
        NotificationUtils.getPermission(object : PermissionListener {
            override fun onPermissionGranted() {
                if (cookProgressStepViewModel.timer.value == null || cookProgressStepViewModel.timer.value == 0) {
                    return
                }
                requireContext().showToast("타이머가 설정되었습니다.")
                val alarmFunctions = AlarmFunctions(requireContext())
                alarmFunctions.callAlarm(cookProgressStepViewModel.timer.value!!,
                    System.currentTimeMillis().toInt(),
                    "요비 타이머")
                cookProgressViewModel.setTimerTtsText("타이머가 설정되었습니다.")
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                requireContext().showToast("권한을 허용해주세요.")
            }
        })
    }
}