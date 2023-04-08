package com.ssafy.data.remote.repository

import com.ssafy.data.remote.datasource.cook.CookDataSource
import com.ssafy.data.remote.mappers.toDomainModel
import com.ssafy.domain.model.cook.CookProgressStepDomainModel
import com.ssafy.domain.repository.CookRepository
import com.ssafy.domain.utils.ViewState
import javax.inject.Inject

internal class CookRepositoryImpl @Inject constructor(private val cookDataSource: CookDataSource) :
    CookRepository {
    override suspend fun getCookProgressList(recipeId: Int): ViewState<List<CookProgressStepDomainModel>> {
        return try {
            val response = cookDataSource.getCookProgressList(recipeId)
            val cookProgressStepDomainModelList = arrayListOf<CookProgressStepDomainModel>()
            if (response.status == 200) {
                response.data?.map {
                    cookProgressStepDomainModelList.add(it.toDomainModel())
                }
                ViewState.Success(response.message, cookProgressStepDomainModelList)
            } else {
                ViewState.Error(response.message, null)
            }
        } catch (e: Exception) {
            ViewState.Error(e.message, null)
        }
    }
}