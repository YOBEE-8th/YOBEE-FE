# 회원정보 관리

---

## 1. 로그아웃

### 1.1 결과화면(GIF)

<img src="https://user-images.githubusercontent.com/48742378/230304672-006e1049-f746-480b-9de0-9ea0d8076b71.gif"  width="200" height="400"/>

---

### 1.2 코드

### Fragment

```kotlin
private fun initLogoutViewModel() {
        myPageViewModel.logoutLiveData.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    showLoadingDialog()
                    Log.d(TAG, "initLogoutViewModel: 로그아웃 처리 진행중")
                }
                is ViewState.Success -> {
                    dismissLoadingDialog()
                    when (response.value!!.type) {
                        0 -> { // 일반 로그아웃
                            navigate(MyPageMainFragmentDirections.actionMyPageMainFragmentToLoginFragment())
                        }
                        1 -> { // 구글 로그아웃
                            val opt =
                                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .build()
                            val client = GoogleSignIn.getClient(mainActivity, opt)
                            client.signOut()
                            navigate(MyPageMainFragmentDirections.actionMyPageMainFragmentToLoginFragment())
                        }
                        2 -> { // TODO 카카오 로그아웃
                            UserApiClient.instance.logout { error ->
                                if (error != null) {
                                    Log.e(TAG, "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                                } else {
                                    Log.i(TAG, "로그아웃 성공. SDK에서 토큰 삭제됨")
                                }
                            }
                            navigate(MyPageMainFragmentDirections.actionMyPageMainFragmentToLoginFragment())
                        }
                    }
                    Log.d(TAG, "initLogoutViewModel: 카카오 로그아웃 성공")
                    requireContext().showToast(response.message!!)
                }
                is ViewState.Error -> {
                    dismissLoadingDialog()
                    requireContext().showToast(response.message!!)
                    Log.d(TAG, "initLogoutViewModel: 카카오 로그아웃 실패 : ${response.message}")
                }
            }
        }
    }
```
---

### ViewModel

```kotlin
@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
   
    private val _logoutLiveData = SingleLiveEvent<ViewState<LogoutDomainModel>>()
    val logoutLiveData: LiveData<ViewState<LogoutDomainModel>> get() = _logoutLiveData

    fun logout() = viewModelScope.launch {
        _logoutLiveData.postValue(ViewState.Loading())
        when (val result = logoutUseCase()) {
            is ViewState.Success -> {
                _logoutLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _logoutLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }
}
```
---

### 1.3 코드 설명

- `**REST API` 통신을 하여 서버에서 현재 로그인 유형(일반,구글,카카오) 타입을 받아와서 `when`문을 통하여 각 유형에 맞게 로그아웃 로직을 구현하였습니다.**
- **로그아웃이 완료되면 `SharedPreference`에 저장되어 있는 accessToken, 회원정보(프로필 이미지, 닉네임)를 삭제합니다.**
- **SNS 로그인 계정인 경우(구글, 카카오) 해당 계정을 로그아웃 하도록 구현하였습니다.**

---

## 2. 계정 탈퇴
---

### 2.1 결과화면 (GIF)

<img src="https://user-images.githubusercontent.com/48742378/230304912-730a1d7b-e9a6-483a-a227-a93c239353d3.gif"  width="200" height="400"/>

---

### 2.2 코드

### Fragment

```kotlin
rivate fun initWithdrawalViewModel() {
    myPageViewModel.withdrawalLiveData.observe(this){response->
when (response) {
            is ViewState.Loading -> {
                showLoadingDialog()
                Log.d(TAG, "initWithdrawalViewModel: 회원탈퇴 처리 진행중")
            }
            is ViewState.Success -> {
                dismissLoadingDialog()
                val email = response.value!!.email

                // 구글 연동 계정이면 구글 계정 해제
                val currentUser = GoogleSignIn.getLastSignedInAccount(requireContext())
                val googleEmail = currentUser?.email.toString()
                if (googleEmail == email) {
                    val opt =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .build()
                    val client = GoogleSignIn.getClient(mainActivity, opt)
                    client.revokeAccess()
                }

                //TODO:카카오 계정으로 연동된 계정이면 카카오 계정 연결 해제
// 연결 끊기
                UserApiClient.instance.unlink{error->
if (error != null) {
                        Log.e(TAG, "연결 끊기 실패", error)
                    } else {
                        Log.i(TAG, "카카오 연결 끊기 성공. SDK에서 토큰 삭제 됨")
                    }
}

Log.d(TAG, "initWithdrawalViewModel: 회원탈퇴 성공")
                requireContext().showToast(response.message!!)
                navigate(MyPageMainFragmentDirections.actionMyPageMainFragmentToLoginFragment())
            }
            is ViewState.Error -> {
                dismissLoadingDialog()
                requireContext().showToast(response.message!!)
                Log.d(TAG, "initWithdrawalViewModel: 회원탈퇴 실패 : ${response.message}")
            }
        }
}
}
```
---

### ViewModel

```kotlin
@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val withdrawalUseCase: WithdrawalUseCase
) : ViewModel() {

    private val _withdrawalLiveData = SingleLiveEvent<ViewState<WithdrawalDomainModel>>()
    val withdrawalLiveData: LiveData<ViewState<WithdrawalDomainModel>> get() = _withdrawalLiveData

    fun withdrawal() = viewModelScope.launch {
        _withdrawalLiveData.postValue(ViewState.Loading())

        when (val result = withdrawalUseCase()) {
            is ViewState.Success -> {
                _withdrawalLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _withdrawalLiveData.postValue(result)
            }
            is ViewState.Loading -> {}
        }
    }
}
```
---

### 2.3 코드 설명

- **`REST API` 통신을 하여 해당 이메일을 받아와 연결되어 있는 계정(일반 로그인, 구글 로그인, 카카오 로그인)을 삭제합니다.**
- **계정탈퇴가 완료되면 `SharedPreference`에 저장되어 있는 accessToken, 회원정보(프로필 이미지, 닉네임)를 삭제합니다.**

---

## 3. 프로필 이미지 변경
---

### 3.1 결과 화면 (GIF)

<img src="https://user-images.githubusercontent.com/48742378/230305094-28916af3-5ef1-4421-8e7b-895c9493b435.gif"  width="200" height="400"/>

---

### 3.2 코드

### Fragment

```kotlin
@AndroidEntryPoint
class MyPageProfileFragment :
    BaseFragment<FragmentMyPageProfileBinding>(R.layout.fragment_my_page_profile) {

    private val myPageProfileViewModel: MyPageProfileViewModel by viewModels()

    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data ?: return@registerForActivityResult
            myPageProfileViewModel.setUserImageUri(imageUri)
            enableCompleteButton()
        }
    }

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun initView() {
        binding.apply {
            myPageProfileFragment = this@MyPageProfileFragment
            myPageProfileViewModel = this@MyPageProfileFragment.myPageProfileViewModel
        }
    }

    override fun initViewModels() {
        myPageProfileViewModel.initData()
        initUserProfileImageObserver()
        initUpdateProfileViewModel()
    }

    // 갤러리 불러오기
    fun getGalleryImage() {
        GalleryUtils.getGallery(requireContext(), imageResult)
    }

    // 이미지 Uri를 Multipart로 변경해서 전송하기 위한 코드
    private fun initUserProfileImageObserver() {
        myPageProfileViewModel.profileImage.observe(this) {
            binding.myPageProfileViewModel = this@MyPageProfileFragment.myPageProfileViewModel
            val file = File(
                GalleryUtils.changeAbsolutelyPath(
                    myPageProfileViewModel.profileImage.value,
                    mainActivity
                )
            )
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("profileImage", file.name, requestFile)
            myPageProfileViewModel.setUserImageUriToMultipart(body)
        }
    }

    private fun initUpdateProfileViewModel() {
        myPageProfileViewModel.updateProfileLiveData.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    showLoadingDialog()
                    Log.d(TAG, "initUpdateProfileViewModel: 프로필 수정 시작")
                }
                is ViewState.Success -> {
                    dismissLoadingDialog()
                    requireContext().showToast(response.message!!)
                    popBackStack()
                    Log.d(TAG, "initUpdateProfileViewModel: 프로필 수정 성공 ${response.message}")
                }
                is ViewState.Error -> {
                    dismissLoadingDialog()
                    requireContext().showToast(response.message!!)
                    Log.d(TAG, "initUpdateProfileViewModel: 프로필 수정 실패 ${response.message}")
                }
            }
        }
    }

    private fun initNicknameStateViewModel() {
        myPageProfileViewModel.nicknameState.observe(this) { state ->
            binding.apply {
                if (!state) {
                    btnMyPageProfileComplete.isEnabled = false
                    enableCompleteButton()
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
class MyPageProfileViewModel @Inject constructor(
    private val getProfileUseCase: getProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
) : ViewModel() {

    private val _profileImage = SingleLiveEvent<Uri>()
    val profileImage: LiveData<Uri>
        get() = _profileImage

    private val _profileImageMultipartBodyLiveData = SingleLiveEvent<MultipartBody.Part>()
    val profileImageMultipartBodyLiveData: LiveData<MultipartBody.Part>
        get() = _profileImageMultipartBodyLiveData

    fun initData() {
        val profile = getProfileUseCase()
        _profileImage.setValue(Uri.parse(profile.profileImage))
    }

    fun setUserImageUri(imageUri: Uri) {
        _profileImage.setValue(imageUri)
    }

    fun setUserImageUriToMultipart(imageUri: MultipartBody.Part) {
        _profileImageMultipartBodyLiveData.postValue(imageUri)
    }

    private val _updateProfileLiveData = SingleLiveEvent<ViewState<ProfileDomainModel>>()
    val updateProfileLiveData: LiveData<ViewState<ProfileDomainModel>> get() = _updateProfileLiveData

    fun updateProfile() = viewModelScope.launch {
        _updateProfileLiveData.postValue(ViewState.Loading())
        when (val result =
            updateProfileUseCase(
                profileImageMultipartBodyLiveData.value,
                nickname.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            )) {
            is ViewState.Success -> {
                _updateProfileLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _updateProfileLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }

    private val _profileImageState = SingleLiveEvent<Boolean>()
    val profileImageState: LiveData<Boolean>
        get() = _profileImageState

    init {
        _profileImageState.postValue(false)
    }
}
```
---

### Util

```kotlin
object GalleryUtils {
    fun getGallery(context: Context, imageLauncher: ActivityResultLauncher<Intent>) { //사진 가져오기
        getPermission(object : PermissionListener {
            override fun onPermissionGranted() {
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                imageLauncher.launch(intent)
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(context, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getPermission(listener: PermissionListener) {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            TedPermission.create()
                .setPermissionListener(listener)
                .setDeniedMessage("권한을 허용해주세요")
                .setPermissions(android.Manifest.permission.READ_MEDIA_IMAGES)
                .check()
        } else {
            TedPermission.create()
                .setPermissionListener(listener)
                .setDeniedMessage("권한을 허용해주세요")
                .setPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check()
        }

    }

    fun changeAbsolutelyPath(path: Uri?, context: Context): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        var result = c?.getString(index!!)
        return result!!
    }
}
```
---

### 3.3 코드 설명

- **프로필 이미지를 클릭하면 권한을 체크를 하고 허용 버튼을 누르면 갤러리로 이동해서 선택한 이미지를 `registerForActivityResult()` 함수를 통해 가져오고 이 과정에서 받아온 uri를 `ViewModel`에 profileImage에 저장합니다.**
- **`LiveData`인 profileImager가 `observer`를 통해 실시간으로 변화를 감지하였을 때, 해당 이미지 Uri를  `MultiPart`타입 파일로 만들어주고 `ViewModel`에 profileImageMultipartBodyLiveData에 저장합니다.**
- **프로필 수정 버튼을 누르면 저장되어있는 `MultiPart`파일인 이미지를 서버와 통신하여 서버에 업로드하여서 저장된 경로를  response 값을 받아와 `SharedPreference`에 프로필 이미지 값에 갱신시켜줍니다.**

---

## 4. 닉네임 변경

---

### 4.1 결과 화면 (GIF)

<img src="https://user-images.githubusercontent.com/48742378/230305448-a23b4d70-0223-497b-841c-074682c796b5.gif"  width="200" height="400"/>

---

### 4.2 코드

### Fragment

```kotlin
@AndroidEntryPoint
class MyPageProfileFragment :
    BaseFragment<FragmentMyPageProfileBinding>(R.layout.fragment_my_page_profile) {

    private val myPageProfileViewModel: MyPageProfileViewModel by viewModels()
    
		private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun initView() {
        binding.apply {
            myPageProfileFragment = this@MyPageProfileFragment
            myPageProfileViewModel = this@MyPageProfileFragment.myPageProfileViewModel

            etMyPageProfileNickname.addTextChangedListener {
                with(tlMyPageProfileNickname) {
                    error = null
                    helperText = null
                }
            }
        }
    }

    override fun initViewModels() {
        myPageProfileViewModel.initData()
        initValidateNickNameViewModel()
        initUpdateProfileViewModel()
        initNicknameStateViewModel()
    }

    private fun initValidateNickNameViewModel() {
        myPageProfileViewModel.validateNickname.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    Log.d(TAG, "initValidateNickNameViewModel: 닉네임 중복 시작")
                }
                is ViewState.Success -> {
                    with(myPageProfileViewModel) {
                        setNickName(binding.etMyPageProfileNickname.text.toString().trim())
                        setNicknameState(true)
                    }
                    binding.apply {
                        with(tlMyPageProfileNickname) {
                            error = null
                            helperText = response.message
                            setHelperTextColor(
                                ColorStateList.valueOf(
                                    ContextCompat.getColor(requireContext(), R.color.atlantis)
                                )
                            )
                        }
                    }
                    binding.etMyPageProfileNickname.hideKeyboard()
                    enableCompleteButton()
                    Log.d(TAG, "initValidateNickNameViewModel: success ${response.message}")
                }
                is ViewState.Error -> {
                    binding.tlMyPageProfileNickname.error = response.message
                    myPageProfileViewModel.setNicknameState(false)
                    enableCompleteButton()
                    Log.d(TAG, "initValidateNickNameViewModel: 인증 실패 ${response.message}")
                }
            }
        }
    }

    private fun initUpdateProfileViewModel() {
        myPageProfileViewModel.updateProfileLiveData.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    showLoadingDialog()
                    Log.d(TAG, "initUpdateProfileViewModel: 프로필 수정 시작")
                }
                is ViewState.Success -> {
                    dismissLoadingDialog()
                    requireContext().showToast(response.message!!)
                    popBackStack()
                    Log.d(TAG, "initUpdateProfileViewModel: 프로필 수정 성공 ${response.message}")
                }
                is ViewState.Error -> {
                    dismissLoadingDialog()
                    requireContext().showToast(response.message!!)
                    Log.d(TAG, "initUpdateProfileViewModel: 프로필 수정 실패 ${response.message}")
                }
            }
        }
    }

    private fun enableCompleteButton() {
        binding.apply {
            btnMyPageProfileComplete.isEnabled = myPageProfileViewModel!!.nicknameState.value!!
        }
    }

    private fun initNicknameStateViewModel() {
        myPageProfileViewModel.nicknameState.observe(this) { state ->
            binding.apply {
                if (!state) {
                    btnMyPageProfileComplete.isEnabled = false
                    enableCompleteButton()
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
class MyPageProfileViewModel @Inject constructor(
    private val getProfileUseCase: getProfileUseCase,
    private val validateNickNameAuthUseCase: ValidateNickNameAuthUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
) : ViewModel() {

    private var _nickname: String = ""
    val nickname: String get() = _nickname

    fun initData() {
        val profile = getProfileUseCase()
        _nickname = profile.nickName
    }

    fun setNickName(nicknameValue: String) {
        _nickname = nicknameValue
    }

    private val _validateNickname = SingleLiveEvent<ViewState<Void>>()
    val validateNickname: LiveData<ViewState<Void>> get() = _validateNickname

    fun validateNickname(nickname: String) = viewModelScope.launch {
        _validateNickname.postValue(ViewState.Loading())
        when (val result = validateNickNameAuthUseCase(nickname)) {
            is ViewState.Success -> {
                _validateNickname.postValue(result)
            }
            is ViewState.Error -> {
                _validateNickname.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }

    private val _updateProfileLiveData = SingleLiveEvent<ViewState<ProfileDomainModel>>()
    val updateProfileLiveData: LiveData<ViewState<ProfileDomainModel>> get() = _updateProfileLiveData

    fun updateProfile() = viewModelScope.launch {
        _updateProfileLiveData.postValue(ViewState.Loading())
        when (val result =
            updateProfileUseCase(
                profileImageMultipartBodyLiveData.value,
                nickname.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            )) {
            is ViewState.Success -> {
                _updateProfileLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _updateProfileLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }

    
    private val _nicknameState = SingleLiveEvent<Boolean>()
    val nicknameState: LiveData<Boolean>
        get() = _nicknameState

    init {
        _nicknameState.postValue(true)
    }

    fun nicknameWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                _nicknameState.postValue(false)
            }

            override fun afterTextChanged(s: Editable) {}
        }
    }

    fun setNicknameState(state: Boolean) = _nicknameState.setValue(state)
}
```
---

### 4.3 코드 설명

- **`DataBinding`을 사용하여서 `ViewModel`에서 `TextWathcer`을 사용해 nicknameState를 체크합니다.**
- **Fragment에서는 `LiveData`인 nicknameState의 값에 따라 UI를 갱신시켜줍니다.**
- **닉네임 중복 확인 버튼을 누르면 유효성 및 중복 체크 기능을 실행하도록 구현하였습니다.**
- **닉네임 변경이나 프로필 이미지 변경이 있을 시 변경 버튼이 활성화되도록 구현하였습니다.**
- **이미지 변경을 하면 변경 버튼이 활성화 되지만 닉네임 텍스트가 변화하면 감지하여 버튼이 비활성화 되고 유효성 검사를 하고 나서 성공하면 변경 버튼이 활성화 되도록 구현하였습니다.**

---

## 5. 비밀번호 변경

---

### 5.1 결과물 (GIF)

<img src="https://user-images.githubusercontent.com/48742378/230305647-ad51b07d-0585-4449-8c61-c14fdfbfa8cd.gif"  width="200" height="400"/>

---

### 5.2 코드

### Fragment

```kotlin
@AndroidEntryPoint
class ChangePasswordFragment :
    BaseFragment<FragmentChangePasswordBinding>(R.layout.fragment_change_password) {

    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()

    override fun initView() {
        binding.apply {
            changePasswordFragment = this@ChangePasswordFragment
            changePasswordViewModel = this@ChangePasswordFragment.changePasswordViewModel
        }
    }

    override fun initViewModels() {
        initCheckPasswordViewModel()
        initCheckPasswordConfirmViewModel()
        initPasswordChangeViewModel()
    

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
```

---

### ViewModel

```kotlin
@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase,
) : ViewModel() {

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

    private var _passwordConfirmState = SingleLiveEvent<Boolean>()
    val passwordConfirmState: LiveData<Boolean>
        get() = _passwordConfirmState

    fun checkConfirm(pwConfirm: String) {
        if (password.isNotEmpty()) {
            if (pwConfirm == password) {
                _passwordConfirmState.postValue(true)
            } else {
                _passwordConfirmState.postValue(false)
            }
        } else {
            _passwordConfirmState.postValue(false)
        }
    }

    private var _checkedPassword: Boolean = false
    val checkedPassword: Boolean
        get() = _checkedPassword

    fun setCheckPassword(state: Boolean) {
        _checkedPassword = state
    }

    private val _changePasswordLiveData = SingleLiveEvent<ViewState<Void>>()
    val changePasswordLiveData: LiveData<ViewState<Void>> get() = _changePasswordLiveData

    fun changePassword() = viewModelScope.launch {
        _changePasswordLiveData.postValue(ViewState.Loading())
        when (val result = changePasswordUseCase(password)) {
            is ViewState.Success -> {
                _changePasswordLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _changePasswordLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }
}
```

---

### 5.3 코드 설명

- `**DataBinding**`을 통해 `**ViewModel**`에서 비밀번호 입력 부분을 **`TextWatcher`**을 통해 정규식 패턴일치 여부를 확인하고 그에따른 결과를 **passwordState**에 담아 Fragment에서는 해당 값에 따라 UI가 갱신되도록 구현하였습니다.
- 비밀번호 확인 체크를 하더라도 **비밀번호 부분을 변경하면 변경 버튼이 다시 비활성화**되고 비밀번호 정규식 패턴과 일치하고 비밀번호 확인이 비밀번호와 일치하면 다시 비밀번호 변경이 활성화되도록 구현하였습니다.

---

## 6. 비밀번호 재설정

---

### 6.1 결과물 (GIF)

<img src="https://user-images.githubusercontent.com/48742378/230308148-c52538f1-25c1-4f1f-a274-5d1eddec2a69.gif"  width="200" height="400"/>

---

### 6.2 코드

### Fragment

```kotlin
@AndroidEntryPoint
class ResetPasswordFragment :
    BaseFragment<FragmentResetPasswordBinding>(R.layout.fragment_reset_password) {

    private val resetPasswordViewModel: ResetPasswordViewModel by viewModels()

    override fun initView() {
        binding.apply {
            resetPasswordViewModel = this@ResetPasswordFragment.resetPasswordViewModel
        }
        setHelpContent()
    }

    override fun initViewModels() {
        initEmailCheckViewModel()
        initResetPasswordViewModel()
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
```
---

### ViewModel

```kotlin
@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase,
) : ViewModel() {

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
                _email = s.toString()
                if (s.isEmpty()) {
                    _emailState.postValue(0)
                } else {
                    if (Pattern.matches(emailValidation, email)) _emailState.postValue(1)
                    else _emailState.postValue(2)
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        }
    }

    private val _resetPasswordLiveData = SingleLiveEvent<ViewState<Void>>()
    val resetPasswordLiveData: LiveData<ViewState<Void>>
        get() = _resetPasswordLiveData

    fun resetPassword() = viewModelScope.launch {
        _resetPasswordLiveData.postValue(ViewState.Loading())
        when (val result = resetPasswordUseCase(email)) {
            is ViewState.Success -> {
                _resetPasswordLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _resetPasswordLiveData.postValue(result)
            }
            is ViewState.Loading -> {

            }
        }
    }
}
```

---

### Util

```kotlin
object TextUtills {
    //textview 특정 text 컬러나 스타일 바꾸기
    fun setSpannableText(text: String, content: String): SpannableString {
        val spannableString = SpannableString(content)
        val start: Int = content.indexOf(text)
        val end = start + text.length

        spannableString.apply {
            setSpan(
                ForegroundColorSpan(Color.parseColor("#FF6347")),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }
}
```

---

### 6.3 코드 설명

- **안내 텍스트를 `SpannableString`을 사용하여서 특정 부분에 글자의 스타일이나 컬러를 커스텀할 수 있도록 구현하였습니다.**
- **이메일 입력 부분을 `DataBinding`을 사용하여 `ViewModel`에서 `TextWathcer`을 통해 이메일 정규식 패턴 검사를 하고 해당 결과를 emailState에 반영하여 `Fragment`에서는 해당 값을 `observe`하여 UI를 갱신시키도록 구현하였습니다.**
- **이메일을 입력 후 비밀번호 재설정하기 버튼을 누르면 해당 이메일로 임시 비밀번호를 발송합니다.**
- **SNS 계정으로 회원가입한 경우에는 기존의 이메일 회원가입에서 연동한 계정이 아니라면 비밀번호 변경을 할 수 없도록 안내 메세지를 토스트로 안내하도록 구현하였습니다.**
