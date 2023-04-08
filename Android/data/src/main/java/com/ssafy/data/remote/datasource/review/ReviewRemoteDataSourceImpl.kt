package com.ssafy.data.remote.datasource.review

import com.ssafy.data.remote.common.BaseResponse
import com.ssafy.data.remote.datasource.review.dto.CreateReviewResponseDto
import com.ssafy.data.remote.datasource.review.dto.GetReviewResponseDto
import com.ssafy.data.remote.datasource.review.dto.UpdateReviewRequestDto
import com.ssafy.data.remote.service.ReviewApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

internal class ReviewRemoteDataSourceImpl @Inject constructor(private val reviewApiService: ReviewApiService) :
    ReviewRemoteDataSource {
    override suspend fun updateReviewLike(reviewId: Int): BaseResponse<Any> {
        return reviewApiService.updateReviewLike(reviewId)
    }

    override suspend fun createReview(
        reviewImage: MultipartBody.Part?,
        createReviewDto: HashMap<String, RequestBody>,
    ): BaseResponse<CreateReviewResponseDto> {
        return reviewApiService.createReview(reviewImage, createReviewDto)
    }

    override suspend fun getReview(reviewId: Long): BaseResponse<GetReviewResponseDto> {
        return reviewApiService.getReview(reviewId)
    }

    override suspend fun deleteReview(reviewId: Int): BaseResponse<Any> {
        return reviewApiService.deleteReview(reviewId)
    }

    override suspend fun updateReview(updateReviewRequestDto: UpdateReviewRequestDto): BaseResponse<Any> {
        return reviewApiService.updateReview(updateReviewRequestDto)
    }
}