package com.ssafy.yobee.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.ssafy.yobee.R
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity_요비"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private var notificationData: String? = null
    private lateinit var navHostFragment: NavHostFragment
    private var isBackGround = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment

        // FCMService에서 intent에 담아준 recommend 값 가져오기
        notificationData = intent.extras?.getString("recommend")

        // null인 경우 : fcm이 백그라운드에서 왔을 때 -> 서버에서 주는 data에서 recommend 값 가져오기
        if (notificationData == null) {
            isBackGround = true
            notificationData = intent.extras?.getString("recommend")
        }

        if (notificationData != null) {
            handleNotificationData(notificationData)
        }
    }

    private fun handleNotificationData(notificationData: String?) {
        if (notificationData == "recommend") {
            mainViewModel.isBackground = isBackGround
            mainViewModel.isFromFcm = true
        }
    }
}
