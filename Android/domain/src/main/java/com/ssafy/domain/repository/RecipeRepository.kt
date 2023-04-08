package com.ssafy.domain.repository

import com.ssafy.domain.model.recipe.RecipeDetailDomainModel
import com.ssafy.domain.model.recipe.RecipeDomainModel
import com.ssafy.domain.model.recipe.RecipeIngredientDomainModel
import com.ssafy.domain.model.recipe.RecipeReviewDomainModel
import com.ssafy.domain.utils.ViewState

interface RecipeRepository {
    suspend fun getRecipeList(
        category: String,
        sort: Int,
        order: Boolean,
        isAI: Boolean,
        page: Int,
    ): ViewState<List<RecipeDomainModel>>

    suspend fun getRecipeIngredient(recipeId: Int): ViewState<RecipeIngredientDomainModel>

    suspend fun getRecipeDetail(recipeId: Int): ViewState<RecipeDetailDomainModel>

    suspend fun getRecipeReview(recipeId: Int): ViewState<List<RecipeReviewDomainModel>>

    suspend fun getSearchedRecipeList(
        keyword: String,
        sort: Int,
        order: Boolean,
        isAI: Boolean,
    ): ViewState<List<RecipeDomainModel>>

    suspend fun addLikeRecipe(recipeId: Int): ViewState<Any>
}