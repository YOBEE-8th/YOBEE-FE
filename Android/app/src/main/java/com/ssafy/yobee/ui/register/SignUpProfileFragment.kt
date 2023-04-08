package com.ssafy.yobee.ui.register

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.MaterialToolbar
import com.ssafy.domain.model.account.Account
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentSignUpProfileBinding
import com.ssafy.yobee.ui.MainActivity
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.register.model.toRegisterUIModel
import com.ssafy.yobee.util.common.GalleryUtils
import com.ssafy.yobee.util.extension.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


private const val TAG = "SignUpProfileFragment_요비"

@AndroidEntryPoint
class SignUpProfileFragment :
    BaseFragment<FragmentSignUpProfileBinding>(R.layout.fragment_sign_up_profile) {

    private val registerViewModel: RegisterViewModel by viewModels()

    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data ?: return@registerForActivityResult
            registerViewModel.setUserImageUri(imageUri)
            Log.d(TAG, "${registerViewModel.profileImage.value}: ")
        }
    }

    private lateinit var callback: OnBackPressedCallback
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backFragment()
            }
        }
        mainActivity.onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun initView() {

        val args: SignUpProfileFragmentArgs by navArgs()
        val accountInfo = args.account

        registerViewModel.setAccountInfo(accountInfo)

        initToolbar()
        binding.apply {
            signUpFragment = this@SignUpProfileFragment
            registerViewModel = this@SignUpProfileFragment.registerViewModel
        }
    }

    override fun initViewModels() {
        initValidateNickNameViewModel()
        initJoinAccountViewModel()
        initUserProfileImageObserver()
        initCheckNicknameViewModel()
        registerViewModel.initProfileImage()
    }

    private fun initToolbar() {
        val toolbar: MaterialToolbar = binding.tbRegister.tbToolbar
        // arg : 타이틀, 백버튼 유무, 백버튼 눌렀을 때 수행할 동작
        toolbar.initToolbar(resources.getString(R.string.title_register), true) {
            backFragment()
        }
    }

    // 뒤로가기
    private fun backFragment() {
        if (registerViewModel.type == 0) { // 일반 로그인일 때
            val account = Account(
                registerViewModel.email,
                registerViewModel.password,
                "",
                "",
                registerViewModel.verificationCode,
                registerViewModel.type,
                emailCheck = registerViewModel.emailCheck,
                passwordCheck = registerViewModel.passwordCheck,
                nicknameCheck = 0
            ).toRegisterUIModel()
            navigate(
                SignUpProfileFragmentDirections.actionSignUpProfileFragmentToRegisterFragment(
                    account
                )
            )
        } else if (registerViewModel.type == 1) { //구글 계정으로 회원가입 진행중일 경우
            val opt = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            val client = GoogleSignIn.getClient(mainActivity, opt)
            client.revokeAccess()
            registerViewModel.clearJoinData()
            popBackStack()
        }
    }

    // 갤러리 불러오기
    fun getGalleryImage() {
        GalleryUtils.getGallery(requireContext(), imageResult)
    }

    // 이미지 Uri를 Multipart로 변경해서 전송하기 위한 코드
    private fun initUserProfileImageObserver() {
        registerViewModel.profileImage.observe(this) {
            binding.registerViewModel = this@SignUpProfileFragment.registerViewModel
            val file = File(
                GalleryUtils.changeAbsolutelyPath(
                    registerViewModel.profileImage.value,
                    mainActivity
                )
            )
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("profileImage", file.name, requestFile)
            Log.d(TAG, "initUserProfileImageObserver 변형된 값: $body")
            registerViewModel.setUserImageUriToMultipart(body)
            registerViewModel.removeImageUrl()
        }
    }

    // 닉네임 길이 유효성 검사
    private fun initCheckNicknameViewModel() {
        registerViewModel.nicknameState.observe(this) { state ->
            binding.apply {
                if (state) tlSignUpProfileNickname.error = null
                else tlSignUpProfileNickname.error =
                    resources.getString(R.string.content_nickname_length_incorrect)
            }
        }
    }

    private fun initValidateNickNameViewModel() {
        registerViewModel.validateNickname.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    Log.d(TAG, "initValidateNickNameViewModel: 닉네임 중복 시작")
                }
                is ViewState.Success -> {
                    registerViewModel.checkedNickName(1)
                    binding.apply {
                        with(tlSignUpProfileNickname) {
                            error = null
                            helperText = response.message
                            setHelperTextColor(
                                ColorStateList.valueOf(
                                    ContextCompat.getColor(requireContext(), R.color.atlantis)
                                )
                            )
                        }
                        etSignUpProfileNickname.hideKeyboard()
                    }
                    enableCompleteButton()
                    Log.d(TAG, "initValidateNickNameViewModel: success ${response.message}")
                }
                is ViewState.Error -> {
                    registerViewModel.checkedNickName(0)
                    binding.tlSignUpProfileNickname.error = response.message
                    Log.d(TAG, "initValidateNickNameViewModel: 인증 실패 ${response.message}")
                }
            }
        }
    }

    // 회원가입
    private fun initJoinAccountViewModel() {
        registerViewModel.joinAccountLiveData.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    showLoadingDialog()
                    Log.d(TAG, "initJoinAccountViewModel: 회원가입 시작")
                }
                is ViewState.Success -> {
                    dismissLoadingDialog()
                    registerViewModel.clearJoinData()
                    navigate(SignUpProfileFragmentDirections.actionSignUpProfileFragmentToHomeFragment())
                    Log.d(TAG, "initJoinAccountViewModel: 회원가입 성공 ${response.message}")
                }
                is ViewState.Error -> {
                    dismissLoadingDialog()
                    requireContext().showToast(response.message!!)
                    Log.d(TAG, "initJoinAccountViewModel: 회원가입 실패 ${response.message}")
                }
            }
        }
    }

    // 완료 버튼 활성화
    private fun enableCompleteButton() {
        binding.btnSignUpProfileComplete.isEnabled = registerViewModel.nicknameCheck == 1
    }
}