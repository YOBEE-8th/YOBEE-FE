package com.ssafy.domain.usecase.review

import com.ssafy.domain.repository.ReviewRepository
import javax.inject.Inject

class GetReviewUseCase @Inject constructor(private val reviewRepository: ReviewRepository) {
    suspend operator fun invoke(reviewId: Long) = reviewRepository.getReview(reviewId)
}