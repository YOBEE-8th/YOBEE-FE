package com.ssafy.domain.usecase.cook

import com.ssafy.domain.repository.CookRepository
import javax.inject.Inject

class GetCookProgressListUseCase @Inject constructor(private val cookRepository: CookRepository) {
    suspend operator fun invoke(recipeId: Int) = cookRepository.getCookProgressList(recipeId)
}