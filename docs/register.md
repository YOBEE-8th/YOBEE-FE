# 회원가입

## 1. 회원가입 (이메일 비밀번호)

### 1.1 결과물 (GIF)

### 회원 정보 입력 및 전체 과정

<img src="https://user-images.githubusercontent.com/48742378/230293696-37e8336f-91f3-49fa-9171-ce604ac518b5.gif"  width="200" height="400"/>

---

### 인증번호 시간 만료시
<img src="https://user-images.githubusercontent.com/48742378/230296021-20cfe882-4154-40c1-b331-a87a52e27fbb.gif"  width="200" height="400"/>

---

### 1.2 코드

### Fragment

```kotlin
@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(R.layout.fragment_register) {

    private val registerViewModel by viewModels<RegisterViewModel>()

    var isStop = false
		// 인증번호 타이머
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
                registerViewModel.clearJoinData() // ViewModel에 있는 정보 clear
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
```
---

### ViewModel

```kotlin
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val checkVerificationCodeUseCase: CheckVerificationCodeUseCase,
) :
    ViewModel() {

    private val emailValidation =
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    private var _email: String = ""
    val email: String
        get() = _email

    private var _emailState = SingleLiveEvent<Int>()
    val emailState: LiveData<Int>
        get() = _emailState

    fun emailWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                _email = s.toString().trim()
                _emailState.postValue(-1)
            }

            override fun afterTextChanged(s: Editable) {}
        }
    }

    fun checkEmailRegularExpression() {
        if (email.isEmpty()) _emailState.postValue(0)
        else {
            if (Pattern.matches(emailValidation, email)) {
                _emailState.postValue(1)
                validateEmail()
            } else _emailState.postValue(2)
        }
    }

    private val _validateEmailLiveData = SingleLiveEvent<ViewState<Void>>()
    val validateEmailLiveData: LiveData<ViewState<Void>> get() = _validateEmailLiveData

    private fun validateEmail() = viewModelScope.launch {
        _validateEmailLiveData.postValue(ViewState.Loading())
        when (val result = validateEmailUseCase(email)) {
            is ViewState.Success -> {
                _validateEmailLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _validateEmailLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }

    private var _verificationCode: String = ""
    val verificationCode: String
        get() = _verificationCode

    private var _verificationCodeState = SingleLiveEvent<Boolean>()
    val verificationCodeState: LiveData<Boolean>
        get() = _verificationCodeState

    fun setVerificationCodeState(state: Boolean) = _verificationCodeState.postValue(state)

    fun checkVerificationCodeState(state: String, code: String) {
        if (state == "확인") checkVerificationCode(code)
        else {
            validateEmail()
            _verificationCodeState.postValue(false)
        }
    }

    private val _checkVerificationCodeLiveData = SingleLiveEvent<ViewState<Void>>()
    val checkVerificationCodeLiveData: LiveData<ViewState<Void>> get() = _checkVerificationCodeLiveData

    private fun checkVerificationCode(code: String) = viewModelScope.launch {
        _checkVerificationCodeLiveData.postValue(ViewState.Loading())
        when (val result = checkVerificationCodeUseCase(code)) {
            is ViewState.Success -> {
                _checkVerificationCodeLiveData.postValue(result)
                _verificationCode = code
            }
            is ViewState.Error -> {
                _checkVerificationCodeLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }

    private var _emailCheck: Int = 0
    val emailCheck: Int
        get() = _emailCheck

    fun checkedEmail(state: Int) {
        _emailCheck = state
    }

    private var _password: String = ""
    val password: String
        get() = _password

    private var _passwordState = SingleLiveEvent<Int>()
    val passwordState: LiveData<Int>
        get() = _passwordState

    fun passwordWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                _password = s.toString()
                if (s.isEmpty()) {
                    _passwordState.postValue(0)
                } else {
                    if (!Pattern.matches(
                            "^(?=.*\\d)(?=.*[~`!@#$%\\^&*()-])(?=.*[a-zA-Z]).{8,20}$", s
                        )
                    ) _passwordState.postValue(1)
                    else _passwordState.postValue(2)
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        }
    }

    private var _passwordConfirmState = SingleLiveEvent<Int>()
    val passwordConfirmState: LiveData<Int>
        get() = _passwordConfirmState

    fun checkConfirm(pwConfirm: String) {
        if (password.isNotEmpty()) {
            if (pwConfirm == password) {
                _passwordConfirmState.postValue(1)
            } else {
                _passwordConfirmState.postValue(0)
            }
        } else {
            _passwordConfirmState.postValue(2)
        }
    }

    private var _passwordCheck: Int = 0
    val passwordCheck: Int
        get() = _passwordCheck

    fun checkedPassword(state: Int) {
        _passwordCheck = state
    }

   
    fun setAccountInfo(account: RegisterUIModel) {
        _email = account.email
        _password = account.password
        _nickname = account.nickname
        _profileImageMultipartBodyLiveData.postValue(null)
        _profileImageUrl = account.profileImgUrl
        _verificationCode = account.verificationCode
        _type = account.type
        _emailCheck = account.emailCheck
        _passwordCheck = account.passwordCheck
        _nicknameCheck = account.nicknameCheck
        Log.d(
            TAG,
            "세팅된 값: $email $password $nickname ${profileImageMultipartBodyLiveData.value} $profileImageUrl $type $emailCheck $passwordCheck $nicknameCheck"
        )
    }

    fun clearJoinData() {
        _email = ""
        _password = ""
        _nickname = ""
        _profileImageMultipartBodyLiveData.postValue(null)
        _profileImageUrl = ""
        fcmToken = ""
        _verificationCode = ""
        _type = 0
        _emailCheck = 0
        _passwordCheck = 0
        _nicknameCheck = 0
    }
}
```
---

### 1.3 코드 설명

- **이메일 입력 부분에는 정규식 패턴 매칭을 사용해서 이메일 형식인지 구분하여 UI을 갱신하도록 구현하였습니다.**
- **이메일 형식인 이메일을 확인 버튼을 누르면 해당 이메일로 인증번호를 발송합니다.**
- **인증번호를 발송하면 `CountDownTimer`을 사용하여 인증번호 남은 시간을 UI에 표시해주고 인증번호 시간이 만료되면 인증번호 확인 버튼이 재전송 버튼으로 텍스트가 바뀌고 해당 버튼을 누르면 다시 인증번호를 발송하도록 구현하였습니다.**
- **인증번호 남은 시간은 `onStop()`인 상태에서도 작동해야하므로 화면이 완전히 없어지는 `onDestroyView()에`서 cancel 하도록 구현하였습니다.**
- **이전 화면으로 돌아가면(뒤로가기) 현재 `ViewModel`에 저장 되어 있는 가입 정보를 초기화 시키도록 구현하였습니다.**
- **모든 유효성 검사를 마친 뒤에 다음 버튼을 누르면 `Navigation safe args`을 사용하여 현재 저장되어 있는 가입 정보를 객체에 담아서 다음 화면에 전달합니다.**
- **다음 화면에서 이메일, 비밀번호 입력 화면으로 넘어가도 해당 정보가 `Navigation Safe Args`을 통해 전달하므로 정보들이 입력되어 있습니다.**

---

## 2. 프로필 정보 입력

### 2.1 결과물 (GIF)

<img src="https://user-images.githubusercontent.com/48742378/230301901-b938bf2b-bbf9-49d4-a55b-dc2d495965ff.gif"  width="200" height="400"/>

---

### 2.2 코드

### Fragment

```kotlin
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
```
---

### ViewModel

```kotlin
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val checkVerificationCodeUseCase: CheckVerificationCodeUseCase,
    private val validateNickNameUseCase: ValidateNickNameUseCase,
    private val joinAccountUseCase: JoinAccountUseCase,
) :
    ViewModel() {

    private val emailValidation =
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    private var _email: String = ""
    val email: String
        get() = _email

    private var _verificationCode: String = ""
    val verificationCode: String
        get() = _verificationCode

    private var _emailCheck: Int = 0
    val emailCheck: Int
        get() = _emailCheck

		private var _password: String = ""
    val password: String
        get() = _password

    private var _passwordCheck: Int = 0
    val passwordCheck: Int
        get() = _passwordCheck

	  private var _profileImageUrl: String? = ""
    val profileImageUrl: String?
        get() = _profileImageUrl

		private val _profileImage = SingleLiveEvent<Uri?>()
    val profileImage: LiveData<Uri?>
        get() = _profileImage

    fun initProfileImage() {
        if (profileImageUrl == "") _profileImage.setValue(Uri.parse("https://yobee.s3.ap-northeast-2.amazonaws.com/default_profile.png"))
        else _profileImage.setValue(Uri.parse(profileImageUrl))
    }

    private val _profileImageMultipartBodyLiveData = SingleLiveEvent<MultipartBody.Part?>()
    val profileImageMultipartBodyLiveData: LiveData<MultipartBody.Part?>
        get() = _profileImageMultipartBodyLiveData

    fun removeImageUrl() {
        _profileImageUrl = ""
    }

    fun setUserImageUri(imageUri: Uri) {
        _profileImage.setValue(imageUri)
    }

    fun setUserImageUriToMultipart(imageUri: MultipartBody.Part) {
        _profileImageMultipartBodyLiveData.postValue(imageUri)
    }

    private var _nickname: String = ""
    val nickname: String get() = _nickname

    fun nicknameCheck(nickname: String) {
        if (nickname.isNotEmpty()) {
            validateNickname(nickname)
            _nicknameState.postValue(true)
        } else _nicknameState.postValue(false)
    }

    private var _nicknameState = SingleLiveEvent<Boolean>()
    val nicknameState: LiveData<Boolean>
        get() = _nicknameState

    fun setNicknameState(state: Boolean) {
        _nicknameState.postValue(state)
    }

    private val _validateNickname = SingleLiveEvent<ViewState<Void>>()
    val validateNickname: LiveData<ViewState<Void>> get() = _validateNickname

    private fun validateNickname(nickname: String) = viewModelScope.launch {
        _validateNickname.postValue(ViewState.Loading())
        when (val result = validateNickNameUseCase(nickname)) {
            is ViewState.Success -> {
                _validateNickname.postValue(result)
                _nickname = nickname
            }
            is ViewState.Error -> {
                _validateNickname.postValue(result)
                _nickname = ""
            }
            is ViewState.Loading -> {

            }
        }
    }

    private var _nicknameCheck: Int = 0
    val nicknameCheck: Int
        get() = _nicknameCheck

    fun checkedNickName(state: Int) {
        _nicknameCheck = state
    }

    private var fcmToken = ""

    private var _type: Int = 0
    val type: Int
        get() = _type

    private val _joinAccountLiveData = SingleLiveEvent<ViewState<AccountnfoDomainModel>>()
    val joinAccountLiveData: LiveData<ViewState<AccountnfoDomainModel>> get() = _joinAccountLiveData

    fun setAccountInfo(account: RegisterUIModel) {
        _email = account.email
        _password = account.password
        _nickname = account.nickname
        _profileImageMultipartBodyLiveData.postValue(null)
        _profileImageUrl = account.profileImgUrl
        _verificationCode = account.verificationCode
        _type = account.type
        _emailCheck = account.emailCheck
        _passwordCheck = account.passwordCheck
        _nicknameCheck = account.nicknameCheck
        Log.d(
            TAG,
            "세팅된 값: $email $password $nickname ${profileImageMultipartBodyLiveData.value} $profileImageUrl $type $emailCheck $passwordCheck $nicknameCheck"
        )
    }

    fun clearJoinData() {
        _email = ""
        _password = ""
        _nickname = ""
        _profileImageMultipartBodyLiveData.postValue(null)
        _profileImageUrl = ""
        fcmToken = ""
        _verificationCode = ""
        _type = 0
        _emailCheck = 0
        _passwordCheck = 0
        _nicknameCheck = 0
    }

    fun joinAccount() {

        fcmToken = getFcmTokenUseCase()

        var requestHashMap: HashMap<String, RequestBody> = HashMap()

        requestHashMap["email"] =
            email.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        requestHashMap["password"] =
            password.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        requestHashMap["nickname"] =
            nickname.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        requestHashMap["profileImageUrl"] =
            profileImageUrl!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        requestHashMap["fcmToken"] =
            fcmToken.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        requestHashMap["type"] =
            type.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

        viewModelScope.launch {
            _joinAccountLiveData.postValue(ViewState.Loading())
            when (val result =
                joinAccountUseCase(profileImageMultipartBodyLiveData.value, requestHashMap)) {
                is ViewState.Success -> {
                    _joinAccountLiveData.postValue(result)
                }
                is ViewState.Error -> {
                    _joinAccountLiveData.postValue(result)
                }
                is ViewState.Loading -> {

                }
            }
        }
    }
}
```
---

### 2.3 코드 설명

- **일반 회원가입 경우에는 프로필 이미지와 닉네임 정보 값이 비어있고 소셜 회원가입인 경우에는 프로필 이미지와 닉네임을 받아와 `DataBinding`을 통해 뷰에 정보를 갱신시켜 보여줍니다.**
- **프로필 이미지 버튼을 클릭하면 갤러리 권한 요청을 하고 허용된 상태라면 갤러리로 이동해 사진을 선택해 프로필 이미지를 적용할 수 있습니다.**
- **적용된 이미지는 `LiveData`인 profileImageUrl에 저장하고 해당 데이터의 변화를 observer을 통해 감지하여 `MultiPart`형태의 해당 이미지를 파일로 생성하고 해당 데이터를profileImageMultipartBodyLiveData에 저장합니다.**
- **모든 유효성 검사가 성공하고 회원가입 버튼을 누르면 `ViewModel`에서 `SharedPreference`에 저장되어있는 fcmToken 값을 가져오고 나머지 회원 정보를 `MultiPart` 형태로 담아서 서버에 요청합니다.**
- **회원가입에 성공하면 서버에서 반환하는 프로필 이미지, 닉네임, 로그인 유형를 `SharedPreference`에 저장하고 홈 화면으로 이동합니다.**
