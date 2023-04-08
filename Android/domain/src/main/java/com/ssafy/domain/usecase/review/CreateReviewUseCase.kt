package com.ssafy.domain.usecase.review

import com.ssafy.domain.repository.ReviewRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class CreateReviewUseCase @Inject constructor(private val reviewRepository: ReviewRepository) {
    suspend operator fun invoke(
        reviewImage: MultipartBody.Part?,
        createReviewDto: HashMap<String, RequestBody>,
    ) = reviewRepository.createReview(reviewImage, createReviewDto)
}