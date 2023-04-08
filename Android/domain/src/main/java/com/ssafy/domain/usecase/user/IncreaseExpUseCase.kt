package com.ssafy.domain.usecase.user

import com.ssafy.domain.repository.UserRepository
import javax.inject.Inject

class IncreaseExpUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(recipeId: Int) = userRepository.increaseExp(recipeId)
}