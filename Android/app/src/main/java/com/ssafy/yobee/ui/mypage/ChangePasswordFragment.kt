package com.ssafy.yobee.ui.mypage

import android.content.res.ColorStateList
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.appbar.MaterialToolbar
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentChangePasswordBinding
import com.ssafy.yobee.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "ChangePasswordFragment_요비"

@AndroidEntryPoint
class ChangePasswordFragment :
    BaseFragment<FragmentChangePasswordBinding>(R.layout.fragment_change_password) {

    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()

    override fun initView() {
        initToolbar()
        binding.apply {
            changePasswordFragment = this@ChangePasswordFragment
            changePasswordViewModel = this@ChangePasswordFragment.changePasswordViewModel
        }
    }

    override fun initViewModels() {
        initCheckPasswordViewModel()
        initCheckPasswordConfirmViewModel()
        initPasswordChangeViewModel()
    }

    private fun initToolbar() {
        val toolbar: MaterialToolbar = binding.tbChangePassword.tbToolbar
        // arg : 타이틀, 백버튼 유무, 백버튼 눌렀을 때 수행할 동작
        toolbar.initToolbar(resources.getString(R.string.title_change_password), true) {
            popBackStack()
        }
    }

    // 비밀번호 유효성 체크
    private fun initCheckPasswordViewModel() {
        changePasswordViewModel.passwordState.observe(this) { state ->
            binding.apply {
                if (state == 0) {
                    tlChangePasswordPassword.error =
                        resources.getString(R.string.content_password_regular_expression)
                    btnChangePasswordNext.isEnabled = false
                    if (etChangePasswordPasswordConfirm.text!!.isNotEmpty()) {
                        changePasswordViewModel!!.setCheckPassword(false)
                        tlChangePasswordPasswordConfirm.error =
                            resources.getString(R.string.content_password_confirm_inCorrect)
                        tlChangePasswordPasswordConfirm.isEnabled = true
                        btnChangePasswordCheckPasswordConfirm.isEnabled = true
                        enableNextButton()
                    }
                } else if (state == 1) {
                    if (etChangePasswordPasswordConfirm.text!!.isNotEmpty()) {
                        tlChangePasswordPasswordConfirm.error =
                            resources.getString(R.string.content_password_confirm_inCorrect)
                        btnChangePasswordCheckPasswordConfirm.isEnabled = true
                        enableNextButton()
                    }
                    changePasswordViewModel!!.setCheckPassword(false)
                    tlChangePasswordPasswordConfirm.isEnabled = true
                    tlChangePasswordPassword.error =
                        resources.getString(R.string.content_password_regular_expression)
                    btnChangePasswordCheckPasswordConfirm.isEnabled = false
                } else if (state == 2) { //비밀번호 형태가 정상일 경우
                    with(tlChangePasswordPassword) {
                        error = null
                        helperText =
                            resources.getString(R.string.content_password_use_ok)
                        setHelperTextColor(
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.atlantis
                                )
                            )
                        )
                    }
                    btnChangePasswordCheckPasswordConfirm.isEnabled = true
                }
            }
        }
    }

    //비밀번호 확인 메소드
    fun initCheckPasswordConfirmViewModel() {
        changePasswordViewModel.passwordConfirmState.observe(this) { state ->
            binding.apply {
                if (state) {
                    changePasswordViewModel!!.setCheckPassword(true)
                    with(tlChangePasswordPasswordConfirm) {
                        error = null
                        helperText =
                            resources.getString(R.string.content_password_confirm_correct)
                        setHelperTextColor(
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.atlantis
                                )
                            )
                        )
                        isEnabled = false
                    }
                    tlChangePasswordPasswordConfirm.isEnabled = false
                    enableNextButton()
                } else {
                    changePasswordViewModel!!.setCheckPassword(false)
                    tlChangePasswordPasswordConfirm.error =
                        resources.getString(R.string.content_password_confirm_inCorrect)
                    enableNextButton()
                }
            }
        }
    }

    // 다음 버튼 활성화
    private fun enableNextButton() {
        binding.btnChangePasswordNext.isEnabled = changePasswordViewModel.checkedPassword == true
    }

    // 비밀번호 변경
    private fun initPasswordChangeViewModel() {
        changePasswordViewModel.changePasswordLiveData.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    Log.d(TAG, "initPasswordChangeViewModel: 비밀번호 변경 통신 시작")
                }
                is ViewState.Success -> {
                    requireContext().showToast(response.message!!)
                    popBackStack()
                    Log.d(TAG, "initPasswordChangeViewModel: 비밀번호 변경 성공")
                }
                is ViewState.Error -> {
                    requireContext().showToast(response.message!!)
                    Log.d(TAG, "initPasswordChangeViewModel: 비밀번호 변경 성공 실패")
                }
            }
        }
    }
}