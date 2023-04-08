# 완성 사진 분석

## 결과물

<image width = 200 src="https://user-images.githubusercontent.com/49333608/230719675-93fb4782-463e-4752-9786-0f88173a9512.gif">

<br>

## 구현 내용

- 유저가 찍은 사진 음식 관련 사진인지 분석
- 음식 사진인 경우 경험치 증가

<br>
<br>

# 1. **카메라 인텐트 실행**

```xml
<!-- AndroidManifest.xml -->

<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="com.ssafy.yobee.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

- 카메라 앱에서 사진 촬영 후 결과를 돌려 받아서 저장할 경로를 지정
  - @xml/file_paths : 저장할 경로가 저장된 xml 파일
- authorities는 {내 패키지 경로}.fileprovider로 지정

```xml
<!-- file_paths.xml -->

<paths>
    <external-path
        name="files"
        path="." />
    <external-files-path
        name="files"
        path="." />
    <cache-path
        name="cache"
        path="." />
    <external-cache-path
        name="cache"
        path="." />
    <files-path
        name="images"
        path="images/" />
</paths>
```

- 파일 경로 지정

```kotlin
// CameraUtils.kt

lateinit var file: File
...
override fun initView() {
    file = CameraUtils.createImageFile(requireContext())
    initCameraLauncher()
    initButtons()
}
```

```kotlin
// CameraUtils.kt

@Throws(IOException::class)
fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {
        // Save a file: path
        absolutePath
    }
}
```

- 사진 촬영 후 생성될 파일을 생성

```kotlin
// CameraUtils.kt

fun launchCamera(context: Context, launcher: ActivityResultLauncher<Intent>, file: File) {
    getCameraPermission(object : PermissionListener {
        override fun onPermissionGranted() {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoUri =
                FileProvider.getUriForFile(context, "com.ssafy.yobee.fileprovider", file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcher.launch(intent)
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            Toast.makeText(context, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
        }
    })
}
```

```kotlin
// CameraUtils.kt

private fun getCameraPermission(listener: PermissionListener) {
    TedPermission.create()
        .setPermissionListener(listener)
        .setDeniedMessage("권한을 허용해주세요")
        .setPermissions(android.Manifest.permission.CAMERA)
        .check()
}
```

- 사진 파일 정보를 포함한 인텐트로 카메라 실행
- 카메라 실행 시 권한을 TedPermission으로 처리

```kotlin
// CookFinishFragment.kt

private fun initCameraLauncher() {
    cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                CoroutineScope(Dispatchers.Main).launch {
                    showLoadingDialog()
                    withContext(Dispatchers.IO) {
                        bitmap = CameraUtils.getBitmapFromFile(requireContext(), file)
                        uri = CameraUtils.getUriFromBitmap(requireContext(), bitmap).toString()
                        cookProgressViewModel.setUri(uri)
                    }
                    withContext(Dispatchers.Main) {
                        dismissLoadingDialog()
                        navigate(CookProgressFragmentDirections.actionCookProgressFragmentToAnalyzeImageFragment())
                    }
                }
            }
        }
}
```

```kotlin
// CameraUtils.kt

// File -> Bitmap
@Throws(IOException::class)
fun getBitmapFromFile(context: Context, file: File): Bitmap {
    return if (Build.VERSION.SDK_INT >= 29) {
        val source: ImageDecoder.Source =
            ImageDecoder.createSource(context.contentResolver, Uri.fromFile(file))
        ImageDecoder.decodeBitmap(source)
    } else {
        MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.fromFile(file))
    }
}

// Bitmap -> Uri
@Throws(IOException::class)
fun getUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(
        context.contentResolver,
        bitmap,
        "title-${Calendar.getInstance().time}",
        null
    )
    return Uri.parse(path)
}
```

- 사진을 찍은 후 확인을 클릭하면 registerForActivityResult로 \*\*\*\*결과값을 받아올 수 있음
- 결과값으로 받아온 file을 file→bitmap→uri로 변경하여 uri를 뷰모델에 저장

<br>

# 2. **카메라로 찍은 이미지 비트맵으로 변환**

```kotlin
// CameraUtils.kt

// Uri -> Bitmap
@Throws(IOException::class)
fun getBitmapFromUri(context: Context, uri: Uri): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
    } else {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
}
```

- 이미지 분석을 위해서는 이미지가 비트맵 형식이어야 함
- 뷰모델에 저장한 uri를 비트맵으로 변환

<br>

# 3. 비트맵으로 이미지 라벨 지정

```kotlin
// AnalyzeImageFragment.kt

private val availableLabel = arrayListOf(
    ...
    "Food",
    ...
)

private val foodLabel = arrayListOf(
    ...
    "Pizza",
    ...
)

private fun analyzePicture(bitmap: Bitmap) {
    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    val image = InputImage.fromBitmap(bitmap, 0)

    labeler.process(image)
        .addOnSuccessListener { labels ->
            var isSuccess = false
            for (label in labels) {
                if (label.text in availableLabel) {
                    if (label.confidence >= 0.65) {
                        isSuccess = true
                        break
                    }
                }
                if (label.text in foodLabel) {
                    if (label.confidence >= 0.65) {
                        isSuccess = true
                        break
                    }
                }
            }
            moveToNextPage(isSuccess)
        }
        .addOnFailureListener { e ->
            Log.d(TAG, "analyzePicture: ${e.printStackTrace()}")
            dismissLoadingDialog()
            navigate(AnalyzeImageFragmentDirections.actionAnalyzeImageFragmentToErrorFragment())
        }
}
```

- ML Kit의 이미지 레이블러와 ML Kit가 이해할 수 있는 형식(InputImage)의 이미지 생성
- 레이블러에 두 개의 리스너 추가
  - 성공 리스너 : 처리가 성공적으로 완료되었을 때
    - 발견된 레이블 목록에서 레이블 텍스트와 신뢰도 값을 비교해 isSuccess 값 세팅
  - 실패 리스너 : 처리를 실패했을 때
    - 예외 처리

<br>

# 4. 경험치 그래프 그리기

```kotlin
// AnalyzeSuccessFragment.kt

private fun initRadarChart() {
    val maxVal = if (incresedExpModel.expList.max() <= 0) {
        1f
    } else {
        incresedExpModel.expList.max()
    }

    val radarData = RadarData()
    radarData.addDataSet(GraphUtils.getBackgroundDataSet(requireContext(), maxVal))

    val valueRadarEntry: ArrayList<RadarEntry> = ArrayList()
    incresedExpModel.expList.forEach {
        valueRadarEntry.add(RadarEntry(it))
    }
    radarData.addDataSet(GraphUtils.getChartDataSet(requireContext(), valueRadarEntry))

    val labels = arrayOf("국/찌개", "면", "디저트", "구이/볶음", "반찬")

    binding.rcAnalyzeSuccessExp.apply {
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

        ...
    }
}
```

```kotlin
// GraphUtils.kt

fun getBackgroundDataSet(context: Context, maxVal: Float): RadarDataSet {
    // 그래프 배경을 위한 데이터
    val bgRadarEntry: ArrayList<RadarEntry> = ArrayList()
    for (i in 0..4) {
        bgRadarEntry.add(RadarEntry(maxVal))
    }

    val bgRadarDataSet = RadarDataSet(bgRadarEntry, "bgRadarEntry")
    bgRadarDataSet.color = ContextCompat.getColor(context, R.color.bittersweet_100)
    bgRadarDataSet.fillColor = ContextCompat.getColor(context, R.color.bittersweet_200)
    bgRadarDataSet.setDrawFilled(true)
    bgRadarDataSet.fillAlpha = 60
    bgRadarDataSet.valueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return ""
        }
    }

    return bgRadarDataSet
}

fun getChartDataSet(context: Context, radarEntry: ArrayList<RadarEntry>): RadarDataSet {
    ...
}
```

- RadarEntry 리스트 생성
  - 배경은 경험치 최댓값으로
    ```kotlin
    val bgRadarEntry: ArrayList<RadarEntry> = ArrayList()
    for (i in 0..4) {
        bgRadarEntry.add(RadarEntry(maxVal))
    }
    ```
- RadarDataSet 생성
  ```kotlin
  val bgRadarDataSet = RadarDataSet(bgRadarEntry, "bgRadarEntry")
  bgRadarDataSet.color = ContextCompat.getColor(context, R.color.bittersweet_100)
  bgRadarDataSet.fillColor = ContextCompat.getColor(context, R.color.bittersweet_200)
  bgRadarDataSet.setDrawFilled(true)
  bgRadarDataSet.fillAlpha = 60
  bgRadarDataSet.valueFormatter = object : ValueFormatter() {
      override fun getFormattedValue(value: Float): String {
          return ""
      }
  }
  ```
- RadarData에 DataSet 추가
  ```kotlin
  val radarData = RadarData()
  radarData.addDataSet(GraphUtils.getBackgroundDataSet(requireContext(), maxVal))
  radarData.addDataSet(GraphUtils.getChartDataSet(requireContext(), valueRadarEntry))
  ```
- 그래프 뷰 속성 설정

  ```kotlin
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
  ```
