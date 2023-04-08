package com.ssafy.data.remote.service

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.review.dto.CreateReviewResponseDto
import com.ssafy.data.remote.datasource.review.dto.GetReviewResponseDto
import com.ssafy.data.remote.datasource.review.dto.UpdateReviewRequestDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

internal interface ReviewApiService {
    @PUT("review/{reviewId}/like")
    suspend fun updateReviewLike(@Path("reviewId") reviewId: Int): BaseResponse<Any>

    @Multipart
    @POST("user/review")
    suspend fun createReview(
        @Part reviewImage: MultipartBody.Part?,
        @PartMap createReviewDto: HashMap<String, RequestBody>,
    ): BaseResponse<CreateReviewResponseDto>

    @GET("user/review/{id}")
    suspend fun getReview(@Path("id") reviewId: Long): BaseResponse<GetReviewResponseDto>

    @DELETE("user/review/{id}")
    suspend fun deleteReview(@Path("id") reviewId: Int): BaseResponse<Any>

    @PUT("user/review")
    suspend fun updateReview(@Body updateReviewRequestDto: UpdateReviewRequestDto): BaseResponse<Any>
}