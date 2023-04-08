# 레시피 소개

## 결과물

<image width = 200 src="https://user-images.githubusercontent.com/49333608/230719011-61122f7f-cc3c-40e6-9cdc-59e23db28595.gif">

<br>

## 구현 내용

- 레시피 소개
- 레시피 좋아요
- 레시피 재료
- 레시피 관련 영상
- 레시피 리뷰
- 레시피 리뷰 좋아요
- 레시피 리뷰 삭제

<br>
<br>

# 1. Chip 동적으로 생성

```xml
<!-- fragment_recipe_detail.xml -->

<com.google.android.material.chip.ChipGroup
    android:id="@+id/cg_recipe_detail_hashtag"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:layout_marginHorizontal="20dp"
    android:layout_marginVertical="10dp"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toBottomOf="@+id/tv_recipe_detail_food">
</com.google.android.material.chip.ChipGroup>
```

```xml
<!-- chip_hashtag.xml -->

<com.google.android.material.chip.Chip xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:clickable="false"
    android:fontFamily="@font/nanum_gothic_bold"
    android:textColor="@color/grey_400"
    android:textSize="12sp"
    app:chipBackgroundColor="@color/white"
    app:textEndPadding="0dp"
    app:textStartPadding="0dp" />
```

```kotlin
// RecipeDetailFragment.kt

        ...
        createChipGroup(recipeDetailModel.hashtag)
    }
}

private fun createChipGroup(hashtag: List<String>) {
    binding.cgRecipeDetailHashtag.apply {
        removeAllViews()
        hashtag.forEach { hashTag ->
            addView(createChip(hashTag))
        }
    }
}

private fun createChip(tag: String): Chip {
    val chip = ChipHashtagBinding.inflate(layoutInflater).root as Chip
    chip.text = "#$tag"
    return chip
}
```

- 서버 통신에서 해시태그 리스트를 받아와 Chip 생성
- 레시피 좋아요를 눌렀을 때 한번 더 서버 통신을 해서 Chip이 중복으로 생성되는 문제를 해결하기 위해 `removeAllViews()` 메서드를 호출해 ChipGroup에 속한 모든 Chip을 삭제
- ChipGroup에 chip_hashtag.xml을 인플레이션하고 텍스트를 설정한 Chip을 `addView()` 메서드를 사용해 추가

<br>

# 2. 유튜브 API

1. Google Cloud Platform에 프로젝트 등록 후 Youtube Data API v3 사용 설정

   - [https://console.cloud.google.com/](https://console.cloud.google.com/)

   ![](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/img/youtube_1.png)

2. API 키 생성

   - API 및 서비스 > 사용자 인증 정보

     ![](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/img/youtube_2.png)

   - 사용자 인증 정보 만들기 > API 키 눌러서 API 키 생성

     ![](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/img/youtube_3.png)

3. API 호출

   ```kotlin
   // Constant.kt

   const val BASE_URL_YOUTUBE = "https://www.googleapis.com/youtube/v3/"
   ```

   ```kotlin
   // VideoApiService.kt

   internal interface VideoApiService {
       @GET("search")
       suspend fun getVideos(
           @Query("part") part: String,
           @Query("maxResults") maxResults: Int,
           @Query("q") q: String,
           @Query("key") key: String,
       ): Response<VideoResponseDto>
   }
   ```

   ```xml
   // appkey.xml

   <?xml version="1.0" encoding="utf-8"?>
   <resources>
       <string name="youtube_api_key">2단계에서 생성한 API 키 붙여넣기</string>
   </resources>
   ```

   ```kotlin
   // RecipeDetailFragment.kt

   binding.apply {
       tlRecipeDetailTabs.addOnTabSelectedListener(object :
           TabLayout.OnTabSelectedListener {
           override fun onTabSelected(tab: TabLayout.Tab?) {
               when (tab?.position) {
               ...
               1 -> {
                   recipeViewModel.getVideos(
                       "snippet",
                       10,
                       args.recipeTitle,
                       getString(R.string.youtube_api_key)
                   )
                   ...
               }
               ...
   ```

<br>

# 3. 리사이클러뷰의 높이 계산해 뷰페이저 높이 동적으로 설정

```kotlin
// IngredientListFragment.kt, VideoListFragment.kt, ReviewListFragment.kt

binding.apply {
    ...
    rvIngredients.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
        recipeViewModel.setIngredientsHeight(
            ViewUtils.getRecyclerViewItemHeight(
                rvIngredients,
                ingredientsListAdapter.itemCount
            ) + ViewUtils.dpToPx(requireContext(), 20f)
        )
    }
}
```

- IngredientListFragment, VideoListFragment, ReviewListFragment에서 각 리사이클러뷰의 addOnLayoutChangeListener가 호출될 때 리사이클러뷰의 높이를 구합니다
  - addOnLayoutChangeListener : RecyclerView 내부 항목의 레이아웃 변경 사항을 감지하고 처리하기 위해 사용

```kotlin
// ViewUtils.kt

fun dpToPx(context: Context, dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}

fun getRecyclerViewItemHeight(recyclerView: RecyclerView, cnt: Int): Int {
    val layoutManager = recyclerView.layoutManager
    var height = 0

    for (i in 0 until cnt) {
        val view = layoutManager?.findViewByPosition(i) ?: continue
        val params = view.layoutParams as RecyclerView.LayoutParams
        view.measure(
            View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        height += view.measuredHeight + params.topMargin + params.bottomMargin
    }
    return height
}
```

- 리사이클러뷰 아이템의 갯수만큼 반복문을 돌면서 인덱스(i)에 해당하는 아이템의 뷰 객체를 가져와 아이템 뷰의 높이와 상하 margin 값들을 반복해서 더해 총 높이를 구함

```kotlin
// RecipeDetailFragment.kt

tlRecipeDetailTabs.addOnTabSelectedListener(object :
    TabLayout.OnTabSelectedListener {
    override fun onTabSelected(tab: TabLayout.Tab?) {
        when (tab?.position) {
            0 -> {
                recipeViewModel.getRecipeIngredient(args.recipeId)
                setHeight(recipeViewModel.ingredientsHeight.value ?: 10000)
            }
            1 -> {
                recipeViewModel.getVideos(
                    "snippet",
                    10,
                    args.recipeTitle,
                    getString(R.string.youtube_api_key_suyong)
                )
                setHeight(recipeViewModel.youtubeHeight.value ?: 10000)
            }
            2 -> {
                recipeViewModel.getRecipeReview(args.recipeId)
                setHeight(recipeViewModel.reviewsHeight.value ?: 10000)
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }
})
```

```kotlin
// RecipeDetailFragment.kt

fun setHeight(height: Int) = lifecycleScope.launch(Dispatchers.Main) {
    binding.vpRecipeDetailInfo.apply {
        layoutParams.height = height
        requestLayout()
    }
}
```

- TabLayout이 선택될 때 뷰모델에 저장한 높이 값을 가져와서 뷰페이저의 높이를 지정
