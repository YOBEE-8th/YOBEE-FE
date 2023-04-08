package com.ssafy.data.remote.repository

import com.ssafy.data.remote.datasource.recipe.dto.RecipeResponseDto
import com.ssafy.data.remote.datasource.user.UserRemoteDataSource
import com.ssafy.data.remote.datasource.user.dto.IncreaseExpRequestDto
import com.ssafy.data.remote.datasource.user.dto.ReviewByDateRequestDto
import com.ssafy.data.remote.mappers.toDomainModel
import com.ssafy.domain.model.recipe.RecipeDomainModel
import com.ssafy.domain.model.user.ExpDomainModel
import com.ssafy.domain.model.user.IncreaseExpDomainModel
import com.ssafy.domain.model.user.ReviewByDateDomainModel
import com.ssafy.domain.model.user.ReviewByRecipeDomainModel
import com.ssafy.domain.repository.UserRepository
import com.ssafy.domain.utils.ViewState
import javax.inject.Inject

private const val TAG = "UserRepositoryImpl_요비"

internal class UserRepositoryImpl @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
) : UserRepository {
    override suspend fun getExp(): ViewState<ExpDomainModel> = try {
        val response = userRemoteDataSource.getExp()
        if (response.status == 200) {
            if (response.data != null) {
                ViewState.Success(response.message, response.data.toDomainModel())
            } else {
                ViewState.Success(response.message, ExpDomainModel(0F, 0F, 0F, 0F, 0F))
            }
        } else {
            ViewState.Error(response.message, null)
        }
    } catch (e: Exception) {
        ViewState.Error(e.message, null)
    }

    override suspend fun increaseExp(recipeId: Int): ViewState<IncreaseExpDomainModel> = try {
        val response = userRemoteDataSource.increaseExp(IncreaseExpRequestDto(recipeId))
        if (response.status == 200) {
            ViewState.Success(response.message, response.data!!.toDomainModel())
        } else {
            ViewState.Error(response.message, null)
        }
    } catch (e: Exception) {
        ViewState.Error(e.message, null)
    }

    override suspend fun getLikeRecipeList(): ViewState<List<RecipeDomainModel>> = try {
        val response = userRemoteDataSource.getLikeRecipeList()
        if (response.status == 200) {
            val recipeDomainModelList = arrayListOf<RecipeDomainModel>()
            for (recipeResponseDto: RecipeResponseDto in response.data!!) {
                recipeDomainModelList.add(recipeResponseDto.toDomainModel())
            }
            ViewState.Success(response.message, recipeDomainModelList)
        } else {
            ViewState.Error(response.message, null)
        }
    } catch (e: Exception) {
        ViewState.Error(e.message, null)
    }

    override suspend fun getReviewByRecipe(): ViewState<List<ReviewByRecipeDomainModel>> = try {
        val response = userRemoteDataSource.getReviewByRecipe()
        val reviewByRecipeDomainModelList = arrayListOf<ReviewByRecipeDomainModel>()

        if (response.status == 200) {
            response.data?.map {
                reviewByRecipeDomainModelList.add(it.toDomainModel())
            }
            ViewState.Success(response.message, reviewByRecipeDomainModelList)
        } else {
            ViewState.Error(response.message, null)
        }
    } catch (e: Exception) {
        ViewState.Error(e.message, null)
    }

    override suspend fun getReviewByDate(recipeId: Long): ViewState<List<ReviewByDateDomainModel>> =
        try {
            val response = userRemoteDataSource.getReviewByDate(ReviewByDateRequestDto(recipeId))
            val reviewByDateDomainModelList = arrayListOf<ReviewByDateDomainModel>()

            if (response.status == 200) {
                response.data?.map {
                    reviewByDateDomainModelList.add(it.toDomainModel())
                }
                ViewState.Success(response.message, reviewByDateDomainModelList)
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(e.message, null)
        }
}