package com.ssafy.data.remote.datasource.review

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.review.dto.CreateReviewResponseDto
import com.ssafy.data.remote.datasource.review.dto.GetReviewResponseDto
import com.ssafy.data.remote.datasource.review.dto.UpdateReviewRequestDto
import okhttp3.MultipartBody
import okhttp3.RequestBody

internal interface ReviewRemoteDataSource {
    suspend fun updateReviewLike(reviewId: Int): BaseResponse<Any>
    suspend fun createReview(
        reviewImage: MultipartBody.Part?,
        createReviewDto: HashMap<String, RequestBody>,
    ): BaseResponse<CreateReviewResponseDto>

    suspend fun getReview(reviewId: Long): BaseResponse<GetReviewResponseDto>
    suspend fun deleteReview(reviewId: Int): BaseResponse<Any>
    suspend fun updateReview(updateReviewRequestDto: UpdateReviewRequestDto): BaseResponse<Any>
}