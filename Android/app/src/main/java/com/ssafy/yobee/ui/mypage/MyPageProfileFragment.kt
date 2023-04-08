package com.ssafy.yobee.ui.mypage

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.google.android.material.appbar.MaterialToolbar
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentMyPageProfileBinding
import com.ssafy.yobee.ui.MainActivity
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.util.common.GalleryUtils
import com.ssafy.yobee.util.extension.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

private const val TAG = "MyPageProfileFragment_요비"

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

    private lateinit var inputMethodManager: InputMethodManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun initView() {
        binding.apply {
            myPageProfileFragment = this@MyPageProfileFragment
            myPageProfileViewModel = this@MyPageProfileFragment.myPageProfileViewModel

            inputMethodManager =
                mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            etMyPageProfileNickname.addTextChangedListener {
                with(tlMyPageProfileNickname) {
                    error = null
                    helperText = null
                }
            }
        }
        initToolbar()
    }

    override fun initViewModels() {
        myPageProfileViewModel.initData()
        initValidateNickNameViewModel()
        initUserProfileImageObserver()
        initUpdateProfileViewModel()
        initNicknameStateViewModel()
    }

    private fun initToolbar() {
        val toolbar: MaterialToolbar = binding.tbMyPageProfile.tbToolbar
        // arg : 타이틀, 백버튼 유무, 백버튼 눌렀을 때 수행할 동작
        toolbar.initToolbar(resources.getString(R.string.content_my_page_main_edit_button), true) {
            popBackStack()
        }
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