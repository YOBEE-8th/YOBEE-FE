# 리뷰

## 결과물

<image width = 200 src="https://user-images.githubusercontent.com/49333608/230719671-5f8b304a-62ac-4763-a369-92e7f724d1d3.gif">
<image width = 200 src="https://user-images.githubusercontent.com/49333608/230719319-bcdd16e8-6df7-46c1-be0f-c23e8d682554.gif">

<br>

## 구현 내용

    - 사용자가 찍은 이미지 리뷰로 등록
    - 리뷰 내용 작성, 수정, 삭제
    - 레시피별, 날짜별 리뷰 확인

<br>
<br>

# 1. 멀티파트로 이미지 서버에 전송

```kotlin
// AnalyzeImageFragment.kt

val file = File(
    GalleryUtils.changeAbsolutelyPath(
        Uri.parse(cookProgressViewModel.uri),
        requireContext()
    )
)

val requestFile = file.asRequestBody("image/ *".toMediaTypeOrNull())
val body =
    MultipartBody.Part.createFormData("reviewImage", file.name, requestFile)
cookProgressViewModel.createReview(body, cookProgressViewModel.recipeId, "")
```

```kotlin
// GalleryUtils.kt

fun changeAbsolutelyPath(path: Uri?, context: Context): String {
    val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
    val c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
    val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    c?.moveToFirst()

    var result = c?.getString(index!!)
    return result!!
}
```

- 사진의 uri로 절대 경로를 얻어와 File 객체 생성
- 생성한 File 객체를 asRequestBody 메서드로 RequestBody 객체로 변환하고, MultipartBody.Part.createFormData 메서드로 MultipartBody.Part 객체로 변환

```kotlin
// CookProgressViewModel.kt

fun createReview(img: MultipartBody.Part, recipeId: Int, content: String) =
    viewModelScope.launch {
        _createReviewLiveData.postValue(ViewState.Loading())
        val reviewHashMap = HashMap<String, RequestBody>()
        reviewHashMap["recipeId"] =
            recipeId.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
        reviewHashMap["content"] =
            content.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        when (val result = createReviewUseCase(img, reviewHashMap)) {
            is ViewState.Success -> {
                _createReviewLiveData.postValue(result)
            }
            is ViewState.Error -> {
                _createReviewLiveData.postValue(result)
            }
            is ViewState.Loading -> {}
        }
    }
```

- 멀티파트 요청에 객체를 보내기 위해 key가 recipeId와 content이고, RequestBody 타입의 값을 가지는 HashMap 객체 생성
  - reviewHashMap 결과 : {recipeId=1, content=hello}
- 데이터(recipeId 또는 content)와 미디어 타입("multipart/form-data".toMediaTypeOrNull())을 이용해 RequestBody 객체를 생성
  - multipart/form-data는 파일 업로드와 함께 전송되는 데이터의 미디어 타입

```kotlin
// ReviewApiService.kt

@Multipart
@POST("user/review")
suspend fun createReview(
    @Part reviewImage: MultipartBody.Part?,
    @PartMap createReviewDto: HashMap<String, RequestBody>,
): BaseResponse<CreateReviewResponseDto>
```

- @Multipart 어노테이션을 붙여 서버에 멀티파트 요청을 보냄
- reviewImage는 MultipartBody.Part 타입의 객체로 @Part 어노테이션을 붙여 파일 또는 이미지 데이터를 전달
- createReviewDto는 MashMap<String, RequestBody> 타입의 객체로 @PartMap 어노테이션을 붙여 key-value 값을 전달
