package com.ssafy.data.remote.repository

import com.ssafy.data.remote.datasource.recommend.RecommendRemoteDataSource
import com.ssafy.data.remote.mappers.toDomainModel
import com.ssafy.domain.model.recommend.RecommendDomainModel
import com.ssafy.domain.repository.RecommendRepository
import com.ssafy.domain.utils.ViewState
import javax.inject.Inject

internal class RecommendRepositoryImpl @Inject constructor(private val recommendRemoteDataSource: RecommendRemoteDataSource) :
    RecommendRepository {
    override suspend fun getRecommendList(): ViewState<List<RecommendDomainModel>> = try {
        val response = recommendRemoteDataSource.getRecommendRecipe()
        if (response.status == 200) {
            val recommendDomainModelList = arrayListOf<RecommendDomainModel>()
            response.data!!.map {
                recommendDomainModelList.add(it.toDomainModel())
            }
            ViewState.Success(response.message, recommendDomainModelList)
        } else {
            ViewState.Error(response.message, null)
        }

    } catch (e: Exception) {
        ViewState.Error(e.message, null)
    }
}