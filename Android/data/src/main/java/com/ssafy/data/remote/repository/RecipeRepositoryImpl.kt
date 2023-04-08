package com.ssafy.data.remote.repository

import com.ssafy.data.remote.datasource.recipe.RecipeRemoteDataSource
import com.ssafy.data.remote.datasource.recipe.dto.RecipeRequestDto
import com.ssafy.data.remote.datasource.recipe.dto.RecipeSearchRequestDto
import com.ssafy.data.remote.mappers.toDomainModel
import com.ssafy.domain.model.recipe.RecipeDetailDomainModel
import com.ssafy.domain.model.recipe.RecipeDomainModel
import com.ssafy.domain.model.recipe.RecipeIngredientDomainModel
import com.ssafy.domain.model.recipe.RecipeReviewDomainModel
import com.ssafy.domain.repository.RecipeRepository
import com.ssafy.domain.utils.ViewState
import javax.inject.Inject

internal class RecipeRepositoryImpl @Inject constructor(private val recipeRemoteDataSource: RecipeRemoteDataSource) :
    RecipeRepository {
    override suspend fun getRecipeList(
        category: String,
        sort: Int,
        order: Boolean,
        isAI: Boolean,
        page: Int,
    ): ViewState<List<RecipeDomainModel>> {
        return try {
            val result =
                recipeRemoteDataSource.getRecipeList(RecipeRequestDto(category,
                    sort,
                    order,
                    isAI,
                    page))
            val recipeDomainModelList = arrayListOf<RecipeDomainModel>()
            if (result.status == 200) {
                result.data?.map {
                    recipeDomainModelList.add(it.toDomainModel())
                }
                ViewState.Success(result.message, recipeDomainModelList)
            } else {
                ViewState.Error(result.message, null)
            }

        } catch (e: Exception) {
            ViewState.Error(e.message, null)
        }
    }

    override suspend fun getRecipeIngredient(recipeId: Int): ViewState<RecipeIngredientDomainModel> {
        return try {
            val response = recipeRemoteDataSource.getRecipeIngredient(recipeId)
            val recipeIngredientDomainModelList = arrayListOf<RecipeIngredientDomainModel>()

            if (response.status == 200) {
                ViewState.Success(response.message, response.data?.toDomainModel())
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(e.message, null)
        }
    }

    override suspend fun getRecipeDetail(recipeId: Int): ViewState<RecipeDetailDomainModel> {
        return try {
            val response = recipeRemoteDataSource.getRecipeDetail(recipeId)

            if (response.status == 200) {
                ViewState.Success(response.message, response.data!!.toDomainModel())
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(e.message, null)
        }
    }

    override suspend fun getRecipeReview(recipeId: Int): ViewState<List<RecipeReviewDomainModel>> {
        return try {
            val response = recipeRemoteDataSource.getRecipeReview(recipeId)
            val recipeReviewDomainModelList = arrayListOf<RecipeReviewDomainModel>()

            if (response.status == 200) {
                response.data?.forEach { review ->
                    recipeReviewDomainModelList.add(review.toDomainModel())
                }
                ViewState.Success(response.message, recipeReviewDomainModelList)
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(e.message, null)
        }
    }

    override suspend fun getSearchedRecipeList(
        keyword: String,
        sort: Int,
        order: Boolean,
        isAI: Boolean,
    ): ViewState<List<RecipeDomainModel>> {
        return try {
            val result = recipeRemoteDataSource.getSearchedRecipeList(
                RecipeSearchRequestDto(
                    keyword,
                    isAI,
                    order,
                    sort
                )
            )
            val recipeDomainModelList = arrayListOf<RecipeDomainModel>()
            if (result.status == 200) {
                result.data?.map {
                    recipeDomainModelList.add(it.toDomainModel())
                }
                ViewState.Success(result.message, recipeDomainModelList)
            } else {
                ViewState.Error(result.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(e.message, null)
        }
    }

    override suspend fun addLikeRecipe(recipeId: Int): ViewState<Any> {
        return try {
            val result = recipeRemoteDataSource.addLikeRecipe(recipeId)
            if (result.status == 200) {
                ViewState.Success(result.message, null)
            } else {
                ViewState.Error(result.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(e.message, null)
        }
    }
}