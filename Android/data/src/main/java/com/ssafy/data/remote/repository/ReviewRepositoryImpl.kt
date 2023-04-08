package com.ssafy.data.remote.repository

import com.ssafy.data.remote.datasource.review.ReviewRemoteDataSource
import com.ssafy.data.remote.datasource.review.dto.UpdateReviewRequestDto
import com.ssafy.data.remote.mappers.toDomainModel
import com.ssafy.domain.model.review.CreateReviewDomainModel
import com.ssafy.domain.model.review.GetReviewDomainModel
import com.ssafy.domain.repository.ReviewRepository
import com.ssafy.domain.utils.ViewState
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

internal class ReviewRepositoryImpl @Inject constructor(private val reviewRemoteDataSource: ReviewRemoteDataSource) :
    ReviewRepository {
    override suspend fun updateReviewLike(reviewId: Int): ViewState<Any> {
        return try {
            val response = reviewRemoteDataSource.updateReviewLike(reviewId)
            if (response.status == 200) {
                ViewState.Success(response.message, null)
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(e.message, null)
        }
    }

    override suspend fun createReview(
        reviewImage: MultipartBody.Part?,
        createReviewDto: HashMap<String, RequestBody>,
    ): ViewState<CreateReviewDomainModel> {
        return try {
            val response = reviewRemoteDataSource.createReview(reviewImage, createReviewDto)
            if (response.status == 200) {
                ViewState.Success(response.message, response.data!!.toDomainModel())
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(e.message, null)
        }
    }

    override suspend fun getReview(reviewId: Long): ViewState<GetReviewDomainModel> {
        return try {
            val response = reviewRemoteDataSource.getReview(reviewId)

            if (response.status == 200) {
                ViewState.Success(response.message, response.data!!.toDomainModel())
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(e.message, null)
        }
    }

    override suspend fun deleteReview(reviewId: Int): ViewState<Any> {
        return try {
            val response = reviewRemoteDataSource.deleteReview(reviewId)

            if (response.status == 200) {
                ViewState.Success(response.message, null)
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(e.message, null)
        }
    }

    override suspend fun updateReview(reviewId: Int, content: String): ViewState<Any> {
        return try {
            val response =
                reviewRemoteDataSource.updateReview(UpdateReviewRequestDto(reviewId, content))

            if (response.status == 200) {
                ViewState.Success(response.message, null)
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(e.message, null)
        }
    }
}