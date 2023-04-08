package com.ssafy.yobee.ui.splash

import android.animation.Animator
import android.animation.ObjectAnimator
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.al.mond.typer.Typer
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentSplashBinding
import com.ssafy.yobee.ui.MainViewModel
import com.ssafy.yobee.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "SplashFragment_요비"

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash) {

    private val splashViewModel by viewModels<SplashViewModel>()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun initView() {
        if (mainViewModel.isFromFcm && !mainViewModel.isBackground) {
            navigate(SplashFragmentDirections.actionSplashFragmentToRecommendFragment())
        }
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