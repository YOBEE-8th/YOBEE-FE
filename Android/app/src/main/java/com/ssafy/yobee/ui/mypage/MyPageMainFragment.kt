package com.ssafy.yobee.ui.mypage

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.user.UserApiClient
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentMyPageMainBinding
import com.ssafy.yobee.ui.MainActivity
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.common.CommonDialog
import com.ssafy.yobee.ui.common.GraphUtils
import com.ssafy.yobee.ui.mypage.model.ExpModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MyPageMainFragment_요비"

@AndroidEntryPoint
class MyPageMainFragment : BaseFragment<FragmentMyPageMainBinding>(R.layout.fragment_my_page_main) {

    private val myPageViewModel: MyPageViewModel by viewModels()
    private lateinit var expModel: ExpModel

    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun initView() {
        binding.myPageMainFragment = this
        initToolbar()
        initClickListener()
        myPageViewModel.getExp()
        myPageViewModel.getNickname()
        myPageViewModel.getProfileImage()
        activateChangePassword()
    }

    override fun initViewModels() {
        initMyPageViewModel()
        initLogoutViewModel()
        initWithdrawalViewModel()
    }

    private fun initToolbar() {
        val toolbar: Toolbar = binding.layoutToolbar.tbToolbar
        toolbar.initToolbar("마이페이지", true) {

            findNavController().navigate(R.id.action_myPageMainFragment_pop)
        }
    }

    private fun initClickListener() {
        binding.apply {
            tvMyPageMainLike.setOnClickListener {
                navigate(MyPageMainFragmentDirections.actionMyPageMainFragmentToLikeRecipeFragment())
            }
        }
    }

    private fun initMyPageViewModel() {
        myPageViewModel.apply {
            expLiveData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Success -> {
                        expModel = response.value!!
                        initRadarChart()
                    }
                    is ViewState.Error -> {
                        Log.d(TAG, "마이페이지 에러 : ${response.message}")
                        navigate(MyPageMainFragmentDirections.actionMyPageMainFragmentToErrorFragment())
                    }
                    is ViewState.Loading -> {
                        Log.d(TAG, "마이페이지 로딩")
                    }
                }
            }
            nickname.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Success -> {
                        setNickname(response.value!!)
                    }
                    is ViewState.Error -> {
                        Log.d(TAG, "마이페이지 - 닉네임 에러 : ${response.message}")
                    }
                    is ViewState.Loading -> {
                        Log.d(TAG, "마이페이지 - 닉네임 로딩")
                    }
                }
            }
            profileImage.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Success -> {
                        setProfileImage(response.value!!)
                    }
                    is ViewState.Error -> {
                        Log.d(TAG, "마이페이지 - 프로필 사진 에러 : ${response.message}")
                    }
                    is ViewState.Loading -> {
                        Log.d(TAG, "마이페이지 - 프로필 사진 로딩")
                    }
                }
            }
        }
    }

    private fun setNickname(nickname: String) {
        binding.tvMyPageMainProfile.text = nickname
    }

    private fun setProfileImage(profileImage: String) {
        Glide.with(requireContext())
            .load(profileImage)
            .placeholder(R.drawable.shimmer)
            .into(binding.ivMyPageMainProfile)
    }

    private fun initRadarChart() {
        val maxVal = if (expModel.expList.max() <= 0) {
            1f
        } else {
            expModel.expList.max()
        }

        val radarData = RadarData()
        radarData.addDataSet(GraphUtils.getBackgroundDataSet(requireContext(), maxVal))

        val valueRadarEntry: ArrayList<RadarEntry> = ArrayList()
        expModel.expList.forEach {
            valueRadarEntry.add(RadarEntry(it))
        }
        radarData.addDataSet(GraphUtils.getChartDataSet(requireContext(), valueRadarEntry))

        val labels = arrayOf("국/찌개", "면", "디저트", "구이/볶음", "반찬")

        binding.rcMyPageMainExp.apply {
            clear()
            notifyDataSetChanged()
            invalidate()

            setExtraOffsets(28f, 20f, 28f, 0f)

            // 그래프 값 범위 설정
            yAxis.apply {
                axisMinimum = 0f
                axisMaximum = maxVal
                setLabelCount(6, true)
            }

            // 데이터와 라벨 설정
            data = radarData
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                textSize = 11f
                textColor = ContextCompat.getColor(requireContext(), R.color.bittersweet_400)
                typeface = Typeface.DEFAULT_BOLD
            }

            // 차트 안쪽 선 색상 설정
            webColor = ContextCompat.getColor(requireContext(), R.color.grey_300)
            webColorInner = ContextCompat.getColor(requireContext(), R.color.grey_300)
            webLineWidth = 0.5f
            webLineWidthInner = 0.5f

            // 회전, 터치 방지
            isRotationEnabled = false
            setTouchEnabled(false)

            // 필요 없는거 안보이게 설정
            legend.isEnabled = false
            description.isEnabled = false
            yAxis.setDrawLabels(false)
        }
    }

    fun onMyReviewClick() {
        Log.d(TAG, "onMyReviewClick: ")
        navigate(MyPageMainFragmentDirections.actionMyPageMainFragmentToReviewByRecipeFragment())
    }

    // 로그아웃
    fun logout() {
        val dialog = CommonDialog(
            requireContext(),
            resources.getString(R.string.content_logout),
            resources.getString(R.string.title_logout)
        ) {
            myPageViewModel.logout()
        }

        showDialog(dialog, viewLifecycleOwner)
    }

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

    // 회원탈퇴
    fun withdrawal() {
        val dialog = CommonDialog(
            requireContext(),
            resources.getString(R.string.content_withdrawal),
            resources.getString(R.string.title_withdrawal)
        ) {
            myPageViewModel.withdrawal()
        }

        showDialog(dialog, viewLifecycleOwner)
    }

    private fun initWithdrawalViewModel() {
        myPageViewModel.withdrawalLiveData.observe(this) { response ->
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

                    // TODO: 카카오 계정으로 연동된 계정이면 카카오 계정 연결 해제
                    // 연결 끊기
                    UserApiClient.instance.unlink { error ->
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

    // 프로필 페이지로 이동
    fun moveProfileFragment() =
        navigate(MyPageMainFragmentDirections.actionMyPageMainFragmentToMyPageProfileFragment())

    // 비밀번호 변경 페이지로 이동
    fun moveChangePasswordFragment() =
        navigate(MyPageMainFragmentDirections.actionMyPageMainFragmentToChangePasswordFragment())

    // 로그인 유형에 따른 비밀번호 변경 활성화
    private fun activateChangePassword() {
        binding.apply {
            if (myPageViewModel.loginType == 0) tvMyPageMainChangePwd.visibility = View.VISIBLE
            else tvMyPageMainChangePwd.visibility = View.GONE
        }
    }

}