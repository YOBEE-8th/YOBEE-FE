package com.ssafy.domain.usecase.recipe

import com.ssafy.domain.repository.RecipeRepository
import javax.inject.Inject

class ChangeRecipeLikeStatusUseCase @Inject constructor(private val recipeRepository: RecipeRepository) {
    suspend operator fun invoke(recipeId: Int) = recipeRepository.addLikeRecipe(recipeId)
}