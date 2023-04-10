# 👨‍🍳 요비(YOBEE) - Android 👨‍🍳

## 1️⃣ Specification

### 📱 Android

| Title | Content                                                                                                         |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Architecture | CleanArchitecture, MVVM, Single Activity Application                                                                                                         |
| DI           | Hilt                                                                                                                                                         |
| Jetpack      | Navigation, ViewBinding, DataBinding, ViewModel, LiveData, Lifecycle                                                                                         |
| Network      | Retrofit2, OkHttp, Gson                                                                                                                                      |
| Library      | Firebase(FCM), Google(Auth), Kakao(Auth), MLKit, Glide, TedPermission, MPAndroidChart, SwipeRefreshLayout, Lottie, PageIndicatorView, SimpleRatingBar, Typer |
| Engine       | Google STT Engine, Google TTS Engine                                                                                                                         |

## 2️⃣ Package Structure

```
📦 com.ssafy.yobee
 ┣ 📂 alarm
 ┣ 📂 ui
 ┃ ┗ 📂 common
 ┃ ┗ 📂 cook
 ┃ ┗ 📂 home
 ┃ ┗ 📂 login
 ┃ ┗ 📂 mypage
 ┃ ┗ 📂 recipe
 ┃ ┗ 📂 recommend
 ┃ ┗ 📂 register
 ┃ ┗ 📂 search
 ┃ ┗ 📂 splash
 ┃ ┗ 📜 MainActivity.kt
 ┃ ┗ 📜 MainViewModel.kt
 ┣ 📂 util
 ┃ ┗ 📂 common
 ┃ ┗ 📂 extension
 ┗ 📜 ApplicationClass.kt
 ┗ 📜 BindingAdapters.kt

📦 com.ssafy.common

📦 com.ssafy.data
 ┣ 📂 local
 ┃ ┗ 📂 local
 ┃ ┃ ┣ 📂 prefs
 ┣ 📂 remote
 ┃ ┗ 📂 common
 ┃ ┗ 📂 datasource
 ┃ ┃ ┗ 📂 dto
 ┃ ┃ ┗ 📂 datasource
 ┃ ┗ 📂 di
 ┃ ┗ 📂 mappers
 ┃ ┗ 📂 repository
 ┃ ┗ 📂 service
 ┗ 📂 util

📦 com.ssafy.domain
 ┣ 📂 model
 ┣ 📂 repository
 ┣ 📂 usecase
 ┗ 📂 utils
```

## 3️⃣ Screenshot
### 시연 영상(클릭시 유튜브 영상으로 이동합니다)

<a href="https://youtu.be/00JqSq2Y96A">
 <image style="height: 350px" src="https://github.com/YOBEE-8th/.github/blob/main/profile/img/yobee_thumbnail.png">
</a>
<br>

### Screenshot
<image style="height: 350px" src="https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/img/screenshot_1.png">

<image style="height: 350px" src="https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/img/screenshot_2.png">
 
<image style="height: 350px" src="https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/img/screenshot_3.png">
 
<image style="height: 350px" src="https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/img/screenshot_4.png">
 
<image style="height: 180px" src="https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/img/screenshot_5.png">
 
<image style="height: 350px" src="https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/img/screenshot_6.png">
 
<image style="height: 350px" src="https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/img/screenshot_7.png">
 
<image style="height: 350px" src="https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/img/screenshot_8.png">
 
## 4️⃣ Role

| [이민하](https://github.com/minha721)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              | [이수용](https://github.com/suyong5713)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       | [조수연](https://github.com/su6378)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| <image style="width: 200px" src="https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/img/minha.JPG">                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | <image style="width: 200px" src="https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/img/suyong.jpeg">                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   | <image style="width: 200px" src="https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/img/sooyun.png">                                                                                                                                                                                                                                                                                                                                                                                                       |
| [홈화면](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/home.md) <br> [레시피 소개](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/recipe_detail.md) <br> [앱 가이드](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/guide.md) <br> [완성 사진 분석](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/analyze_image.md) <br> [리뷰](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/review.md) <br> [FCM](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/fcm.md) | [레시피 목록](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/recipe_list.md) <br> [요리 과정(TTS)](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/cook_progress.md) <br> [Clean Architecture](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/clean_architecture) <br> [검색](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/search.md) <br> [메뉴 월드컵](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/menu_worldcup.md) <br> [즐겨찾기](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/favorite.md) | [회원가입](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/register.md) <br> [로그인](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/login.md) <br> [회원 정보 관리](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/account_management.md) <br> [음성 제어 & 질문](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/stt.md) <br> [스플래쉬](https://github.com/YOBEE-8th/.github/blob/main/profile/android_contents/splash.md) |

