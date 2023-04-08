# 스플래시

---

## 1. 스플래시

### 결과물(GIF)

### 토큰 유효성 검사 실패 화면                        

<img src="https://user-images.githubusercontent.com/48742378/230302429-356eea2e-46b4-4ab8-b640-81cd2edfdaa4.gif"  width="200" height="400"/>

---

### 토큰 유효성 검사 성공 화면
<img src="https://user-images.githubusercontent.com/48742378/230302520-00b21d9e-ba43-4608-996f-da96c5ed14ac.gif"  width="200" height="400"/>

---

### 1.2 코드

### Fragment

```kotlin
@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    private val splashViewModel by viewModels<SplashViewModel>()
		
		override fun initView() {
        startSplashEffect()
    }

    override fun initViewModels() {
        initValidateTokenViewModel()
    }

    // 스플래시 효과
    private fun startSplashEffect() {
        binding.apply {
            Typer.typing(tvSplashTitle, resources.getString(R.string.content_splash_title), false) {
                Typer.stop()
                val fadeIn = ObjectAnimator.ofFloat(ivSplashAppName, "alpha", 0f, 1f)
                fadeIn.duration = 1500
                fadeIn.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        ivSplashAppName.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        initLoginType()
                        initGetFcmToken()
                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }

                })
                fadeIn.start()
            }
        }
    }

    // FCM 토큰 발급받기
    private fun initGetFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    Log.d(TAG, "initFcm: FCM Token : $token")
                    splashViewModel.addFcmToken(token)
                    Log.d(TAG, "onNewToken: addToken success")
                }
                withContext(Dispatchers.Main) {
                    checkLogin()
                }
            }
        }
    }

    // 로그인 유형 체크
    private fun initLoginType() = splashViewModel.autoLoginCheck()

    private fun checkLogin() {
        // 일반 로그인
        if (splashViewModel.autoLoginCheckValue == "0") navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
        else validateToken()
    }

    // Access, Refresh 토큰 유효성 검사
    private fun validateToken() {
        if (splashViewModel.getAccessToken() == null || splashViewModel.getRefreshToken() == null) { //로그인 페이지로 이동
            navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
        } else {
            splashViewModel.validateToken()
        }
    }

    private fun initValidateTokenViewModel() {
        splashViewModel.validateTokenLiveData.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    Log.d(TAG, "initValidateTokenViewModel: 토큰 유효성 검사 시작")
                }
                is ViewState.Success -> {
                    if (mainViewModel.isFromFcm) {
                        navigate(SplashFragmentDirections.actionSplashFragmentToRecommendFragment())
                    } else {
                        navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
                    }
                    Log.d(TAG, "initValidateTokenViewModel: 토큰 유효성 검사 성공 ${response.message}")
                }
                is ViewState.Error -> {
                    navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                    Log.d(TAG, "initValidateTokenViewModel: 토큰 유효성 검사 실패 ${response.message}")
                }
            }
        }
    }
}
```
---

### ViewModel

```kotlin
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val addFcmTokenUseCase: AddFcmTokenUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getRefreshTokenUseCase: GetRefreshTokenUseCase,
    private val validateTokenUseCase: ValidateTokenUseCase,
    private val autoLoginCheckUseCase: AutoLoginCheckUseCase,
) :
    ViewModel() {

    private var accessToken: String? = ""
    private var refreshToken: String? = ""
    var autoLoginCheckValue: String? = ""

    private val _validateTokenLiveData = SingleLiveEvent<ViewState<AuthDomainModel>>()
    val validateTokenLiveData: LiveData<ViewState<AuthDomainModel>> get() = _validateTokenLiveData

    fun getAccessToken(): String? {
        accessToken = getAccessTokenUseCase.invoke()
        return accessToken
    }

    fun getRefreshToken(): String? {
        refreshToken = getRefreshTokenUseCase.invoke()
        return refreshToken
    }

    fun addFcmToken(token: String) {
        addFcmTokenUseCase.invoke(token)
    }

    fun autoLoginCheck() {
        autoLoginCheckValue = autoLoginCheckUseCase.invoke()
    }

    fun validateToken() = viewModelScope.launch {
        _validateTokenLiveData.postValue(ViewState.Loading())
        when (val result = validateTokenUseCase(accessToken!!, refreshToken!!)) {
            is ViewState.Success -> {
                _validateTokenLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _validateTokenLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }
}
```
---

### 1.3 코드 설명

- **Typer 라이브러리(타자 치듯이 나오는 텍스트 효과)와 ObjectAnimator(fade in)를 사용하여 애니메이션 효과를 적용했습니다.**
- **애니메이션이 끝나고 난 후 통신하여 요청해서 발급받은 FCM 토큰 값을 SharedPreference에 저장합니다.**
- **SharedPreference에 저장되어있는 로그인 타입을 가져와 자동 로그인 체크한 일반 로그인이나 소셜 로그인한 기록이 없으면 로그인 화면으로 이동하도록 구현하였습니다.**
- **반대로 기록이 있으면 토큰 유효성 검사를 해서 서버에 SharedPreference에 저장되어있는 accessToken을 OkHttpClient 라이브러리를 통해 헤더에 담아 요청을 해서 토큰 유효성 검사 api를 구현하였습니다.**
- **accessToken이 유효한 경우에는 홈 화면으로 이동하도록 구현하였습니다.**
- **accessToken이 만료된 경우에는 서버에서 발급한 accessToken으로 SharedPreferece에 토큰 값을 변경하여주고 홈 화면으로 이동하고, accessToken과 refreshToken  둘 다 만료된 경우에는 안내 토스트 메세지와 함께 로그인 화면으로 이동하도록 구현하였습니다.**
