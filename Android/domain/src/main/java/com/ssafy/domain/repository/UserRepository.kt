package com.ssafy.domain.repository

import com.ssafy.domain.model.recipe.RecipeDomainModel
import com.ssafy.domain.model.user.ExpDomainModel
import com.ssafy.domain.model.user.IncreaseExpDomainModel
import com.ssafy.domain.model.user.ReviewByDateDomainModel
import com.ssafy.domain.model.user.ReviewByRecipeDomainModel
import com.ssafy.domain.utils.ViewState

interface UserRepository {
    suspend fun getExp(): ViewState<ExpDomainModel>
    suspend fun increaseExp(recipeId: Int): ViewState<IncreaseExpDomainModel>
    suspend fun getReviewByRecipe(): ViewState<List<ReviewByRecipeDomainModel>>
    suspend fun getReviewByDate(recipeId: Long): ViewState<List<ReviewByDateDomainModel>>
    suspend fun getLikeRecipeList(): ViewState<List<RecipeDomainModel>>
}