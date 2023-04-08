package com.ssafy.yobee.ui.home

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.FragmentHomeBinding
import com.ssafy.yobee.ui.MainActivity
import com.ssafy.yobee.ui.common.BaseFragment
import com.ssafy.yobee.ui.home.adapter.BannerViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

private const val TAG = "HomeFragment_요비"

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    lateinit var job: Job
    private var curBannerPage = 0

    private lateinit var callback: OnBackPressedCallback
    private lateinit var mainActivity: MainActivity

    private val homeViewModel: HomeViewModel by viewModels()

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
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        homeViewModel.getProfileImage()
        initBannerViewPager()
        binding.homeFragment = this
    }

    override fun initViewModels() {
        initHomeViewModel()
    }

    private fun initHomeViewModel() {
        homeViewModel.apply {
            profileImage.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is ViewState.Success -> {
                        val profileImg = response.value!!
                        initToolbar(profileImg)
                    }
                    is ViewState.Error -> {
                        Log.d(TAG, "홈 - 프로필 사진 에러 : ${response.message}")
                    }
                    is ViewState.Loading -> {
                    }
                }
            }
        }
    }

    private fun initToolbar(profileImg: String) {
        binding.tbHome.apply {
            inflateMenu(R.menu.menu_home)

            Glide.with(this)
                .asBitmap()
                .load(profileImg)
                .placeholder(R.drawable.shimmer)
                .circleCrop()
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?,
                    ) {
                        menu.findItem(R.id.action_my_page).icon =
                            BitmapDrawable(resources, resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_search -> {
                        navigate(HomeFragmentDirections.actionHomeFragmentToRecipeSearchFragment())

                        true
                    }
                    R.id.action_my_page -> {
                        navigate(HomeFragmentDirections.actionHomeFragmentToMyPageMainFragment())
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun initBannerViewPager() {
        binding.apply {
            vpHomeBanner.adapter = BannerViewPagerAdapter()
            vpHomeBanner.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            vpHomeBanner.isUserInputEnabled = false
            tlHomeBanner.count = 3

            vpHomeBanner.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tlHomeBanner.selection = position % 3
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)

                    if (state == ViewPager2.SCROLL_STATE_IDLE && !job.isActive) {
                        autoScrollJobCreate()
                    }
                }
            })
        }
    }

    fun onAllCategoryClick() {
        navigate(HomeFragmentDirections.actionHomeFragmentToRecipeListFragmentAndBottomSheet("all"))
    }

    fun onSoupCategoryClick() {
        navigate(HomeFragmentDirections.actionHomeFragmentToRecipeListFragmentAndBottomSheet("soup"))
    }

    fun onSideCategoryClick() {
        navigate(HomeFragmentDirections.actionHomeFragmentToRecipeListFragmentAndBottomSheet("side"))
    }

    fun onGrilledCategoryClick() {
        navigate(HomeFragmentDirections.actionHomeFragmentToRecipeListFragmentAndBottomSheet("grilled"))
    }

    fun onNoodleCategoryClick() {
        navigate(HomeFragmentDirections.actionHomeFragmentToRecipeListFragmentAndBottomSheet("noodle"))
    }

    fun onDessertCategoryClick() {
        navigate(HomeFragmentDirections.actionHomeFragmentToRecipeListFragmentAndBottomSheet("dessert"))
    }

    fun onRecommendClick() {
        navigate(HomeFragmentDirections.actionHomeFragmentToRecommendFragment())
    }


    private fun autoScrollJobCreate() {
        // 화면 이동시 코루틴 취소하기 위해 job 사용
        job = lifecycleScope.launchWhenResumed {
            delay(2000)
            curBannerPage += 1

            if (curBannerPage == Int.MAX_VALUE) {
                curBannerPage = 0
            }

            binding.vpHomeBanner.currentItem = curBannerPage
        }
    }

    override fun onResume() {
        super.onResume()
        autoScrollJobCreate()
    }

    override fun onPause() {
        super.onPause()
        job.cancel()
    }
}