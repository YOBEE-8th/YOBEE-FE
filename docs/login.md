# 로그인

### 1. 일반 로그인

### 1.1 결과물 (GIF)

<img src="https://user-images.githubusercontent.com/48742378/230296735-b0b3a14e-7fb8-4a39-8a16-a879803eff2a.gif"  width="200" height="400"/>

--- 

### 1.2 코드

### Fragment

```kotlin
@RequiresApi(Build.VERSION_CODES.Q)
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val loginViewModel: LoginViewModel by viewModels()

    // 이메일 정규식
    val emailValidation =
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun initView() {
        binding.apply {
            loginFragment = this@LoginFragment

            etLoginEmail.addTextChangedListener { // 텍스트 입력 시 에러 메세지 감추기
                tlLoginEmail.error = null
            }

            etLoginPw.addTextChangedListener {  // 텍스트 입력 시 에러 메세지 감추기
                tlLoginPw.error = null
            
        }
    }

    override fun initViewModels() {
        initLoginViewModel()
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
}
```
---

### ViewModel

```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
) :
    ViewModel() {

    private var fcmToken: String = ""
		
		private val _loginLiveData = SingleLiveEvent<ViewState<UserInfoDomainModel>>()
    val loginLiveData: LiveData<ViewState<UserInfoDomainModel>> get() = _loginLiveData

    fun getFcmToken() {
        fcmToken = getFcmTokenUseCase.invoke()
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginLiveData.postValue(ViewState.Loading())
        when (val result = loginUseCase(email, password, fcmToken)) {
            is ViewState.Success -> {
                _loginLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _loginLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }
}
```
---

### 1.3 코드 설명

- **이메일과 비밀번호 입력 부분을 정규식 패턴과 일치하는지 검사하고 결과에 따른 UI를 갱신하도록 구현하였습니다.**
- **서버와 통신하여 로그인에 성공하면 홈 화면으로 이동하고 서버에서 받은 accessToken 값을 `SharedPreference`에 저장합니다.**

---

## 2. 자동 로그인

### 2.1 결과물 (GIF)

### 자동 로그인 체크 X                                     

<img src="https://user-images.githubusercontent.com/48742378/230297743-d452318a-b179-4cec-bdfb-1b87924212f6.gif"  width="200" height="400"/>

---

### 자동 로그인 체크 O
<img src="https://user-images.githubusercontent.com/48742378/230298814-008de2c5-cf9c-4a8a-b731-1418558fad9b.gif"  width="200" height="400"/>

---

### 2.2 코드

```kotlin
@RequiresApi(Build.VERSION_CODES.Q)
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val loginViewModel: LoginViewModel by viewModels()
   
    override fun initViewModels() {
        initAutoLoginViewModel()
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
                if (cbLoginAutoLogin.isChecked) { // 자동 로그인
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
}
```
---

### 2.3 코드 설명

- **사용자가 자동 로그인 체크박스를 체크하고 로그인을  하면 로그인 성공시에 서버에서 받은 accessToken을 `SharedPreference`에 저장해서 앱을 다시 실행하였을 때 스플래시 화면에서 토큰 유효성 검사를 해서 유효한 accessToken이면 홈 화면으로 이동합니다.**
- **자동 로그인 체크박스를 해제하고 로그인하면 `SharedPreference`에 accessToken 값을 저장하지 않기 때문에 스플래시 화면에서 로그인 화면으로 이동합니다.**

---

## 3. 구글 로그인 (소셜 로그인)

### 3.1 결과물 (GIF)

### 회원가입이 안 한 상태                                     

<img src="https://user-images.githubusercontent.com/48742378/230300285-3a8d94b5-c46f-437c-aa8a-512f7e265130.gif"  width="200" height="400"/>

---

### 회원가입 한 상태
<img src="https://user-images.githubusercontent.com/48742378/230300742-62b4e721-8d4d-42af-a3b9-3cebac303495.gif"  width="200" height="400"/>

---

### 3.2 코드

```kotlin
@RequiresApi(Build.VERSION_CODES.Q)
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(R.layout.fragment_login) {

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var launcher: ActivityResultLauncher<Intent>

    private var googleEmail: String = ""
    private var googleNickName: String = ""
    private var googleProfileImg: String = ""

    override fun initView() {
        binding.apply {
            loginFragment = this@LoginFragment
						initLauncher()
        }
    }

    override fun initViewModels() {
        initSocialLoginViewModel()
    }

    private fun moveHomeFragment() {
        navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
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

    // ActivityResultLauncher 초기화
    private fun initLauncher() {
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(), ActivityResultCallback { result ->
                if (result.resultCode == RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        task.getResult(ApiException::class.java)?.let { account -> //계정 정보 받아오기
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
}
```

---

### 3.3 코드 설명

- **구글 로그인 버튼을 누르면 구글 로그인 화면이 나오고 해당 계정으로 선택하면 `Fragment`에서**

`**registerForActivityResult()`을 통해 앱 내에서 사용할 정보 이메일, 프로필 이미지, 이름 정보를 받아옵니다.**

- **서버와 통신을 해서 해당 계정으로 가입한 이력이 있으면 홈 화면으로 이동하고 가입 되지 않은 계정이면 회원가입 페이지(프로필 입력)로 현재 저장된 구글 계정정보를 `Navigation safe args`를 사용하여 정보를 담고 있는 `Parcelable`을 상속받는 객체를 전달하고 이동합니다.**
