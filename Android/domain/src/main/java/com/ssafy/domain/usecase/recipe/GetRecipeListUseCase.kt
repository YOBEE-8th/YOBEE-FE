package com.ssafy.domain.usecase.recipe

import com.ssafy.domain.repository.RecipeRepository
import javax.inject.Inject

class GetRecipeListUseCase @Inject constructor(private val recipeRepository: RecipeRepository) {
    suspend operator fun invoke(
        category: String,
        sort: Int,
        order: Boolean,
        isAI: Boolean,
        page: Int,
    ) =
        recipeRepository.getRecipeList(category, sort, order, isAI, page)
}