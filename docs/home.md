# 홈화면

## 결과물

<image width = 200 src="https://user-images.githubusercontent.com/49333608/230718778-3cf7d448-2804-4893-ad17-f26f3c7899d5.gif">

<br>

## 구현 내용

- 툴바
- carousel banner

<br>
<br>

# 1. 툴바에 서버 이미지 적용

```kotlin
// HomeFragment.kt

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
    ...
```

- 툴바 메뉴에 적용할 이미지를 서버에서 URL로 받아와 Glide로 처리

<br>

# 2. infinite carousel banner

```kotlin
// BannerViewPagerAdapter.kt

class BannerViewPagerAdapter :
    RecyclerView.Adapter<BannerViewPagerAdapter.BannerViewPagerViewHolder>() {

    inner class BannerViewPagerViewHolder(private val binding: ViewPagerBannerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                when (position % 3) {
                    0 -> ivBannerImage.setImageResource(R.drawable.banner_1)
                    1 -> ivBannerImage.setImageResource(R.drawable.banner_2)
                    2 -> ivBannerImage.setImageResource(R.drawable.banner_3)
                }
            }
        }
    }

    ...

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}
```

- 뷰페이저의 아이템이 이미지뷰 밖에 없기 때문에 RecyclerView의 어댑터를 상속 받아 구현
- infinite carousel banner를 구현할 것이기 때문에 아이템 카운트를 `Int.MAX_VALUE` 로 설정
- 뷰홀더에서는 포지션에 따라 1, 2, 3번 배너 지정

```kotlin
// HomeFragment.kt

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
```

- 뷰페이저의 OnPageChangeCallback에서 오버라이드한 onPageSelected 메서드에서 페이지 선택시 탭 레이아웃의 포지션을 지정
- 뷰페이저의 OnPageChangeCallback에서 오버라이드한 onPageScrollStateChanged 메서드에서 페이지 스크롤 상태가 변경되었을 때 페이지를 이동시키는 job을 생성하는 autoScrollJobCreate() 메소드 실행

```kotlin
// HomeFragment.kt

lateinit var job: Job

...

private fun autoScrollJobCreate() {
    job = lifecycleScope.launchWhenResumed {
        delay(2000)
        curBannerPage += 1

        if (curBannerPage == Int.MAX_VALUE) {
            curBannerPage = 0
        }

        binding.vpHomeBanner.currentItem = curBannerPage
    }
}

...

override fun onPause() {
    super.onPause()
    job.cancel()
}
```

- 현재 액티비티나 프래그먼트가 **resume** 상태인 경우에만 코루틴을 실행할 수 있도록 `lifecycleScope.launchWhenResumed` 함수 안에서 2초 딜레이&뷰페이저의 페이지를 1씩 증가하는 작업을 실행
- 화면 이동시에는 코루틴을 취소하기 위해 job을 사용, onPause 상태일 때 `job.cancel()` 실행
