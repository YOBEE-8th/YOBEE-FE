package com.ssafy.yobee.ui.login

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.ssafy.domain.model.account.Account
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.alarm.AlarmFunctions
import com.ssafy.yobee.databinding.FragmentLoginBinding
import com.ssafy.yobee.ui.MainActivity
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.register.model.toRegisterUIModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern


private const val TAG = "LoginFragment_요비"

@RequiresApi(Build.VERSION_CODES.Q)
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val loginViewModel: LoginViewModel by viewModels()

    // 이메일 정규식
    val emailValidation =
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    private lateinit var launcher: ActivityResultLauncher<Intent>

    private var googleEmail: String = ""
    private var googleNickName: String = ""
    private var googleProfileImg: String = ""

    private lateinit var callback: OnBackPressedCallback
    private lateinit var mainActivity: MainActivity

    val kakaoCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
            loginViewModel.getKakaoAccountInfo(token.accessToken)
            loginViewModel.kakaoAccountInfo.email
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mainActivity.finish()
            }
        }
        mainActivity.onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun initView() {
        val alarmFunctions = AlarmFunctions(requireContext())
        binding.apply {
            loginFragment = this@LoginFragment

            etLoginEmail.addTextChangedListener { // 텍스트 입력 시 에러 메세지 감추기
                tlLoginEmail.error = null
            }

            etLoginPw.addTextChangedListener {  // 텍스트 입력 시 에러 메세지 감추기
                tlLoginPw.error = null
            }

            btnLoginKakaoLogin.setOnClickListener {
                initKakaoLogin()
            }
            initLauncher()

            tvLoginFindPw.setOnClickListener {
            }
        }
    }

    override fun initViewModels() {
        initLoginViewModel()
        initAutoLoginViewModel()
        initSocialLoginViewModel()
    }

    private fun initLoginViewModel() {
        loginViewModel.loginLiveData.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    showLoadingDialog()
                    Log.d(TAG, "initLoginViewModel: get token...")
                }
                is ViewState.Success -> {
                    dismissLoadingDialog()
                    moveHomeFragment()
                    Log.d(TAG, "initLoginViewModel: success ${response.value}")
                }
                is ViewState.Error -> {
                    dismissLoadingDialog()
                    requireContext().showToast(response.message!!)
                    Log.d(TAG, "initLoginViewModel: token error ${response.message}")
                }
            }
        }
    }

    private fun initAutoLoginViewModel() {
        loginViewModel.autoLoginLiveData.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    showLoadingDialog()
                    Log.d(TAG, "initAutoLoginViewModel: 자동 로그인 처리 대기")
                }
                is ViewState.Success -> {
                    dismissLoadingDialog()
                    moveHomeFragment()
                    Log.d(TAG, "initAutoLoginViewModel: 로그인 성공 ${response.value}")
                }
                is ViewState.Error -> {
                    dismissLoadingDialog()
                    requireContext().showToast(response.message!!)
                    Log.d(TAG, "initAutoLoginViewModel: 로그인 실패 ${response.message}")
                }
            }
        }
    }

    private fun moveHomeFragment() {
        navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
    }

    //이메일 유효성 체크 메소드
    fun checkEmail() {
        binding.apply {
            var email = etLoginEmail.text.toString().trim() //공백제거
            val e = Pattern.matches(emailValidation, email)
            //이메일이 비어있는 경우
            if (email.isEmpty()) {
                binding.tlLoginEmail.error =
                    resources.getString(R.string.content_input_email_is_empty)
                return
            } else {
                //이메일 형태가 정상일 경우
                if (e) checkPassword()
                else binding.tlLoginEmail.error =
                    resources.getString(R.string.content_input_email_not_correct)
            }
        }
    }

    //비밀번호 유효성 체크 메소드
    private fun checkPassword() {
        binding.apply {
            var password = etLoginPw.text.toString().trim()
            if (password.isEmpty()) {
                requireContext().showToast(resources.getString(R.string.content_input_password_is_empty))
                return
            } else {
                loginViewModel.getFcmToken()
                if (cbLoginAutoLogin.isChecked) {
                    loginViewModel.autoLogin(
                        etLoginEmail.text.toString().trim(),
                        etLoginPw.text.toString().trim(),
                    )
                } else {
                    loginViewModel.login(
                        etLoginEmail.text.toString().trim(),
                        etLoginPw.text.toString().trim(),
                    )
                }
                return
            }
        }
    }

    // 구글 로그인 화면
    fun initGoogleLogin() {
        binding.run {
            CoroutineScope(Dispatchers.IO).launch {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(mainActivity, gso)
                val signInIntent: Intent = googleSignInClient.signInIntent
                launcher.launch(signInIntent)
            }
        }
    }

    fun initKakaoLogin() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(
                        requireContext(),
                        callback = kakaoCallback
                    )
                } else if (token != null) {
                    loginViewModel.getKakaoAccountInfo(token.accessToken)
                    loginViewModel.kakaoAccountInfo.email
                    Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = kakaoCallback)
        }
    }

    // ActivityResultLauncher 초기화
    private fun initLauncher() {
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(), ActivityResultCallback { result ->
                if (result.resultCode == RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        task.getResult(ApiException::class.java)?.let { account ->
                            googleEmail = account.email.toString()
                            googleNickName = account.displayName.toString()
                            googleProfileImg = account.photoUrl.toString()
                            loginViewModel.socialLogin(googleEmail, 1)
                            Log.d(
                                TAG,
                                "이메일 : ${account.email}  이름 : ${account.displayName} 프로필 사진: ${account.photoUrl}"
                            )
                        } ?: throw Exception()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }


    private fun initSocialLoginViewModel() {
        loginViewModel.socialLoginLiveData.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    showLoadingDialog()
                    Log.d(TAG, "initSocialLoginViewModel: 소셜 로그인 처리 대기")
                }
                is ViewState.Success -> {
                    dismissLoadingDialog()
                    moveHomeFragment()
                    Log.d(TAG, "initSocialLoginViewModel: 소셜 로그인 성공 ${response.value}")
                }
                is ViewState.Error -> {
                    dismissLoadingDialog()
                    if (response.message == "1") {
                        moveProfileFragment(googleEmail, googleNickName, googleProfileImg, 1)
                    } else if (response.message == "2") {
                        loginViewModel.kakaoAccountInfo.apply {
                            if (email == "") {
                                moveProfileFragment(id.toString(), nickName, profileImage, 2)
                            } else {
                                moveProfileFragment(email ?: "", nickName, profileImage, 2)
                            }
                        }
                    } else {
                        requireContext().showToast(response.message!!)
                    }
                }
            }
        }
    }

    // 회원가입 페이지로 이동 (일반 회원가입)
    fun moveRegisterFragment() {
        val account = Account().toRegisterUIModel()
        navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment(account))
    }

    // 프로필 페이지로 이동 (구글 로그인)
    private fun moveProfileFragment(
        socialEmail: String,
        socialNickName: String,
        socialProfileImg: String,
        type: Int,
    ) {
        val account = Account(
            socialEmail, "", socialNickName, socialProfileImg, "", type,
            emailCheck = 0,
            passwordCheck = 0,
            nicknameCheck = 0
        ).toRegisterUIModel()

        navigate(LoginFragmentDirections.actionLoginFragmentToSignUpProfileFragment(account))
    }

    // 비밀번호 재설정 페이지로 이동
    fun moveResetPasswordFragment() =
        navigate(LoginFragmentDirections.actionLoginFragmentToResetPasswordFragment())
}