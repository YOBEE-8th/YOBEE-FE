package com.ssafy.domain.repository

import com.ssafy.domain.model.review.CreateReviewDomainModel
import com.ssafy.domain.model.review.GetReviewDomainModel
import com.ssafy.domain.utils.ViewState
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ReviewRepository {
    suspend fun updateReviewLike(reviewId: Int): ViewState<Any>
    suspend fun createReview(
        reviewImage: MultipartBody.Part?,
        createReviewDto: HashMap<String, RequestBody>,
    ): ViewState<CreateReviewDomainModel>

    suspend fun getReview(reviewId: Long): ViewState<GetReviewDomainModel>
    suspend fun deleteReview(reviewId: Int): ViewState<Any>
    suspend fun updateReview(reviewId: Int, content: String): ViewState<Any>
}