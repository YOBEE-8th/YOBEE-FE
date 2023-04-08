package com.ssafy.domain.usecase.recipe

import com.ssafy.domain.repository.RecipeRepository
import javax.inject.Inject

class GetSearchedRecipeListUseCase @Inject constructor(private val recipeRepository: RecipeRepository) {
    suspend operator fun invoke(keyword: String, sort: Int, order: Boolean, isAI: Boolean) =
        recipeRepository.getSearchedRecipeList(keyword, sort, order, isAI)
}