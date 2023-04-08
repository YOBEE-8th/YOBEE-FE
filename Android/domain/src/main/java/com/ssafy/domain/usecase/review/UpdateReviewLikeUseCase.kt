package com.ssafy.domain.usecase.review

import com.ssafy.domain.repository.ReviewRepository
import javax.inject.Inject

class UpdateReviewLikeUseCase @Inject constructor(private val reviewRepository: ReviewRepository) {
    suspend operator fun invoke(reviewId: Int) = reviewRepository.updateReviewLike(reviewId)
}