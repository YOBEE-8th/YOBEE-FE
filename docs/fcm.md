# FCM

## 결과물

<image width = 200 src="https://user-images.githubusercontent.com/49333608/230719683-0ae14874-9539-45fc-955d-f0e0307e98f6.gif">

<br>

## 구현 내용

    - FCM 받아서 notification 띄우기
    - notification 클릭시 메뉴 추천 테스트 화면으로 이동

<br>
<br>

# 1. notification 띄우기

```kotlin
// FCMService.kt

override fun onMessageReceived(message: RemoteMessage) {
    super.onMessageReceived(message)
    if (message.notification != null) {
        title = message.notification!!.title.toString()
        content = message.notification!!.body.toString()
    }

    sendNotification(title, content)
}
```

```kotlin
// FCMService.kt

private fun sendNotification(title: String, content: String) {
    val fcmIntent = Intent(this, MainActivity::class.java).apply {
        putExtra(FCM_EXTRA, FCM_EXTRA)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent =
        PendingIntent.getActivity(this, 0, fcmIntent, PendingIntent.FLAG_IMMUTABLE)

    val notificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createNotificationChannel(notificationManager)
    }

    val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(R.drawable.ic_fcm)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .build()

    notificationManager.notify(Random.nextInt(), builder)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun createNotificationChannel(notificationManager: NotificationManager) {
    val channelName = CHANNEL_NAME
    val channel = NotificationChannel(
        CHANNEL_ID,
        channelName,
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = CHANNEL_NAME
        enableLights(true)
        lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
    }

    notificationManager.createNotificationChannel(channel)
}

companion object {
    private const val CHANNEL_ID = "YOBEE"
    private const val CHANNEL_NAME = "YOBEE FCM"
    private const val FCM_EXTRA = "recommend"
}
```

- onMessageReceived 메서드로 파이어베이스에서 푸시 알림 메시지를 수신하고, sendNotification 메서드에서 notification을 만들어 알림을 전송합니다
- 안드로이드 8.0(Oreo)부터는 채널을 생성하지 않으면 알림이 전송되지 않으므로, createNotificationChannel() 함수를 사용하여 채널을 생성해 알림을 전송합니다

<br>

# 2. notificaiton 클릭시 화면 이동

```kotlin
// MainActivity.kt

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

		...
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
```

- 포그라운드에서 FCM 받은 경우
  - sendNotification에서 생성한 Intent가 MainActivity를 가리키고 있어 notification 클릭시 MainActivity로 진입
  - Intent의 extra로 담아준 `recommend` 값을 가지고와 notificationData 변수에 설정
- 백그라운드에서 FCM 받은 경우
  - 백그라운드에서 FCM을 받은 경우는 onMessageReceived가 호출되지 않아 FCMService.kt쪽 로직이 실행되지 X → 이 경우 알림은 안드로이드 시스템에서 알림을 생성하고 표시
    - notification의 data로 임의의 값을 받아와 notificationData 변수를 설정
- notificationData의 값이 설정되었으면 mainViewModel에 isBackground(알림을 포그라운드or백그라운드 중 어디서 받았는지), isFromFcm(애플리케이션이 아이콘 클릭or알림 클릭 중 어떻게 실행되었는지) 값을 설정

```kotlin
// SplashFragment.kt

override fun initView() {
    if (mainViewModel.isFromFcm && !mainViewModel.isBackground) {
        navigate(SplashFragmentDirections.actionSplashFragmentToRecommendFragment())
    }
    startSplashEffect()
}
```

- SplashFragment에서 MainActivity에서 설정한 mainViewModel.isFromFcm과 mainViewModel.isBackground 값을 확인해 알림 클릭으로 앱을 실행한 경우 추천페이지로 이동
