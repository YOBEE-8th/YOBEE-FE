package com.ssafy.yobee.ui.register

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.MaterialToolbar
import com.ssafy.domain.model.account.Account
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentRegisterBinding
import com.ssafy.yobee.ui.MainActivity
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.register.model.toRegisterUIModel
import com.ssafy.yobee.util.extension.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*


private const val TAG = "RegisterFragment_요비"

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(R.layout.fragment_register) {

    private val registerViewModel by viewModels<RegisterViewModel>()

    var isStop = false

    private var countDownTimer = object : CountDownTimer(300000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            updateRemainTime(millisUntilFinished)
        }

        override fun onFinish() {
            binding.apply {
                tlRegisterVerificationCode.error =
                    resources.getString(R.string.content_verification_code_time_out)
                registerViewModel!!.setVerificationCodeState(true)
                tlRegisterVerificationCode.helperText = null
            }
        }
    }

    private lateinit var callback: OnBackPressedCallback
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                registerViewModel.clearJoinData()
                popBackStack()
            }
        }
        mainActivity.onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer.cancel()
    }

    override fun initView() {
        binding.apply {

            registerFragment = this@RegisterFragment
            registerViewModel = this@RegisterFragment.registerViewModel

            val args: RegisterFragmentArgs by navArgs()
            val accountInfo = args.account

            registerViewModel!!.setAccountInfo(accountInfo)

            if (accountInfo.email.isNotEmpty()) initAccountInfo() //view에 사용자 정보 갱신

            enableNextButton()
            initToolbar()
        }
    }

    override fun initViewModels() {
        initEmailStateViewModel()
        initValidateEmailViewModel()
        initCheckVerificationCodeViewModel()
        initVerificationCodeStateViewModel()
        initCheckPasswordViewModel()
        initCheckPasswordConfirmViewModel()
    }

    private fun initToolbar() {
        val toolbar: MaterialToolbar = binding.tbRegister.tbToolbar
        // arg : 타이틀, 백버튼 유무, 백버튼 눌렀을 때 수행할 동작
        toolbar.initToolbar(resources.getString(R.string.title_register), true) {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) { //타이머 끄고 이동하기
                    countDownTimer.cancel()
                }
                withContext(Dispatchers.Main) {
                    registerViewModel.clearJoinData()
                    popBackStack()
                }
            }
        }
    }

    // 계정 정보 초기화
    private fun initAccountInfo() {
        binding.apply {
            etRegisterEmail.setText(registerViewModel!!.email)
            etRegisterPassword.setText(registerViewModel!!.password)
            etRegisterVerificationCode.setText(registerViewModel!!.verificationCode)
            etRegisterPasswordConfirm.setText(registerViewModel!!.password)
            if (registerViewModel!!.emailCheck == 1) btnRegisterEmailCheck.isEnabled = false
        }
    }

    // 이메일 정규식 체크
    private fun initEmailStateViewModel() {
        registerViewModel.emailState.observe(this) { state ->
            binding.apply {
                when (state) {
                    -1 -> tlRegisterEmail.error = null
                    0 -> tlRegisterEmail.error =
                        resources.getString(R.string.content_input_email_is_empty)
                    1 -> tlRegisterEmail.error = null
                    2 -> tlRegisterEmail.error =
                        resources.getString(R.string.content_input_email_not_correct)
                }
            }
        }
    }

    private fun initValidateEmailViewModel() {
        registerViewModel.validateEmailLiveData.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    Log.d(TAG, "initValidateEmailViewModel: 이메일 인증 시작")
                }
                is ViewState.Success -> {
                    binding.apply {
                        with(tlRegisterEmail) {
                            error = null
                            helperText = response.message
                            setHelperTextColor(
                                ColorStateList.valueOf(
                                    ContextCompat.getColor(requireContext(), R.color.atlantis)
                                )
                            )
                            isEnabled = false
                        }
                        btnRegisterEmailCheck.isEnabled = false
                        btnRegisterCheckVerificationCode.isEnabled = true
                        countDownTimer.start()
                        etRegisterEmail.hideKeyboard()
                    }
                    Log.d(TAG, "initValidateEmailViewModel: success ${response.message}")
                }
                is ViewState.Error -> {
                    binding.tlRegisterEmail.error = response.message
                    Log.d(TAG, "initValidateEmailViewModel: 인증 실패 ${response.message}")
                }
            }
        }
    }

    //인증번호 남은 시간
    @SuppressLint("SetTextI18n")
    private fun updateRemainTime(remainMillis: Long) {
        if (!isStop) {
            val remainSeconds = remainMillis / 1000
            binding.tlRegisterVerificationCode.helperText =
                "${resources.getString(R.string.title_left_verification_code)}  ${
                    "%02d".format(remainSeconds / 60)
                } : ${"%02d".format(remainSeconds % 60)}"
        }
    }

    //인증번호 체크
    private fun initCheckVerificationCodeViewModel() {
        registerViewModel.checkVerificationCodeLiveData.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    Log.d(TAG, "initCheckVerificationCodeViewModel: 인증번호 검사 시작")
                }
                is ViewState.Success -> {
                    binding.apply {
                        registerViewModel!!.checkedEmail(1)
                        with(tlRegisterVerificationCode) {
                            error = null
                            helperText = response.message
                            setHelperTextColor(
                                ColorStateList.valueOf(
                                    ContextCompat.getColor(requireContext(), R.color.atlantis)
                                )
                            )
                            isEnabled = false
                        }
                        btnRegisterCheckVerificationCode.isEnabled = false
                        countDownTimer.cancel()
                        enableNextButton()
                        etRegisterVerificationCode.hideKeyboard()
                    }
                    Log.d(TAG, "initCheckVerificationCodeViewModel: 인증번호 일치 ${response.message}")
                }
                is ViewState.Error -> {
                    isStop = true
                    binding.tlRegisterVerificationCode.error = response.message

                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2000)

                        isStop = false
                    }

                    Log.d(TAG, "initCheckVerificationCodeViewModel: 인증번호 불일치 ${response.message}")
                }
            }
        }
    }

    // 인증번호 상태 체크 : 재전송 or 확인
    private fun initVerificationCodeStateViewModel() {
        registerViewModel.verificationCodeState.observe(this) { state ->
            binding.apply {
                if (state) btnRegisterCheckVerificationCode.text =
                    resources.getString(R.string.title_resend)
                else {
                    btnRegisterCheckVerificationCode.text = resources.getString(R.string.title_ok)
                    tlRegisterVerificationCode.error = null
                }
            }
        }
    }

    // 비밀번호 유효성 체크
    private fun initCheckPasswordViewModel() {
        registerViewModel.passwordState.observe(this) { state ->
            binding.apply {
                if (state == 0) {
                    tlRegisterPassword.error =
                        resources.getString(R.string.content_password_regular_expression)
                    btnRegisterCheckPasswordConfirm.isEnabled = false
                    if (etRegisterPasswordConfirm.text!!.isNotEmpty()) {
                        registerViewModel!!.checkedPassword(0)
                        tlRegisterPasswordConfirm.error =
                            resources.getString(R.string.content_password_confirm_inCorrect)
                        tlRegisterPasswordConfirm.isEnabled = true
                        btnRegisterCheckPasswordConfirm.isEnabled = true
                        enableNextButton()
                    }
                } else if (state == 1) {
                    if (etRegisterPasswordConfirm.text!!.isNotEmpty()) {
                        registerViewModel!!.checkedPassword(0)
                        tlRegisterPasswordConfirm.error =
                            resources.getString(R.string.content_password_confirm_inCorrect)
                        tlRegisterPasswordConfirm.isEnabled = true
                        btnRegisterCheckPasswordConfirm.isEnabled = true
                        enableNextButton()
                    }
                    tlRegisterPassword.error =
                        resources.getString(R.string.content_password_regular_expression)
                    btnRegisterCheckPasswordConfirm.isEnabled = false
                } else if (state == 2) { //비밀번호 형태가 정상일 경우
                    with(tlRegisterPassword) {
                        error = null
                        helperText = resources.getString(R.string.content_password_use_ok)
                        setHelperTextColor(
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(), R.color.atlantis
                                )
                            )
                        )
                    }
                    btnRegisterCheckPasswordConfirm.isEnabled = true
                }
            }
        }
    }

    //비밀번호 확인 메소드
    private fun initCheckPasswordConfirmViewModel() {
        registerViewModel.passwordConfirmState.observe(this) { state ->
            binding.apply {
                when (state) {
                    0 -> {
                        registerViewModel!!.checkedPassword(0)
                        tlRegisterPasswordConfirm.error =
                            resources.getString(R.string.content_password_confirm_inCorrect)
                        enableNextButton()
                    }
                    1 -> {
                        registerViewModel!!.checkedPassword(1)
                        with(tlRegisterPasswordConfirm) {
                            error = null
                            helperText =
                                resources.getString(R.string.content_password_confirm_correct)
                            setHelperTextColor(
                                ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        requireContext(), R.color.atlantis
                                    )
                                )
                            )
                            isEnabled = false
                        }
                        btnRegisterCheckPasswordConfirm.isEnabled = false
                        etRegisterPasswordConfirm.hideKeyboard()
                        enableNextButton()
                    }
                    2 -> {
                        registerViewModel!!.checkedPassword(0)
                        tlRegisterPassword.error =
                            resources.getString(R.string.content_password_regular_expression)
                        enableNextButton()
                    }
                }
            }
        }
    }

    // 다음 버튼 활성화
    private fun enableNextButton() {
        binding.btnRegisterNext.isEnabled =
            registerViewModel.emailCheck == 1 && registerViewModel.passwordCheck == 1
    }

    //다음 페이지로 이동
    fun moveFragment() {
        binding.apply {
            val email = registerViewModel!!.email
            val password = registerViewModel!!.password
            val verificationCode = registerViewModel!!.verificationCode
            val account = Account(
                email,
                password,
                "",
                "",
                verificationCode,
                0,
                emailCheck = registerViewModel!!.emailCheck,
                passwordCheck = registerViewModel!!.passwordCheck,
                nicknameCheck = 0
            ).toRegisterUIModel()
            navigate(
                RegisterFragmentDirections.actionRegisterFragmentToSignUpProfileFragment(
                    account
                )
            )
        }
    }
}