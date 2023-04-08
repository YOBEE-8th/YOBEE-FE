package com.ssafy.domain.usecase.review

import com.ssafy.domain.repository.ReviewRepository
import javax.inject.Inject

class UpdateReviewUseCase @Inject constructor(private val reviewRepository: ReviewRepository) {
    suspend operator fun invoke(reviewId: Int, content: String) =
        reviewRepository.updateReview(reviewId, content)
}