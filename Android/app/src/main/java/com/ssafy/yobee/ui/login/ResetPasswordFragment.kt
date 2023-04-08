package com.ssafy.yobee.ui.login

import android.util.Log
import androidx.fragment.app.viewModels
import com.google.android.material.appbar.MaterialToolbar
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentResetPasswordBinding
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.util.common.TextUtills
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "ResetPasswordFragment_요비"

@AndroidEntryPoint
class ResetPasswordFragment :
    BaseFragment<FragmentResetPasswordBinding>(R.layout.fragment_reset_password) {

    private val resetPasswordViewModel: ResetPasswordViewModel by viewModels()

    override fun initView() {
        binding.apply {
            resetPasswordViewModel = this@ResetPasswordFragment.resetPasswordViewModel
        }
        initToolbar()
        setHelpContent()
    }

    override fun initViewModels() {
        initEmailCheckViewModel()
        initResetPasswordViewModel()
    }

    private fun initToolbar() {
        val toolbar: MaterialToolbar = binding.tbResetPassword.tbToolbar
        // arg : 타이틀, 백버튼 유무, 백버튼 눌렀을 때 수행할 동작
        toolbar.initToolbar(resources.getString(R.string.title_reset_password), true) {
            popBackStack()
        }
    }

    // 안내 내용 텍스트 설정
    private fun setHelpContent() {
        binding.tvResetPasswordTitle.text = TextUtills.setSpannableText(
            resources.getString(R.string.content_reset_password_help_strong),
            resources.getString(R.string.content_reset_password_help)
        )
    }

    // 이메일 유효성 체크
    private fun initEmailCheckViewModel() {
        resetPasswordViewModel.emailState.observe(this) { state ->
            binding.apply {
                when (state) {
                    0 -> {
                        tlResetPasswordEmail.error =
                            resources.getString(R.string.content_input_email_is_empty)
                        btnResetPassword.isEnabled = false
                    }
                    1 -> {
                        tlResetPasswordEmail.error = null
                        btnResetPassword.isEnabled = true
                    }
                    2 -> {
                        tlResetPasswordEmail.error =
                            resources.getString(R.string.content_input_email_not_correct)
                        btnResetPassword.isEnabled = false
                    }
                }
            }
        }
    }

    private fun initResetPasswordViewModel() {
        resetPasswordViewModel.resetPasswordLiveData.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    Log.d(TAG, "initResetPasswordViewModel: 임시 비밀번호 발급 통신중")
                }
                is ViewState.Success -> {
                    requireContext().showToast(response.message!!)
                    popBackStack()
                    Log.d(TAG, "initGoogleLoginViewModel: 임시 비밀번호 발급 성공 ${response.value}")
                }
                is ViewState.Error -> {
                    requireContext().showToast(response.message!!)
                    Log.d(TAG, "initGoogleLoginViewModel: 임시 비밀번호 발급 실패 ${response.message}")
                }
            }
        }
    }
}