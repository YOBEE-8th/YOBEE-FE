package com.ssafy.domain.repository

import com.ssafy.domain.model.cook.CookProgressStepDomainModel
import com.ssafy.domain.utils.ViewState

interface CookRepository {
    suspend fun getCookProgressList(recipeId: Int): ViewState<List<CookProgressStepDomainModel>>
}