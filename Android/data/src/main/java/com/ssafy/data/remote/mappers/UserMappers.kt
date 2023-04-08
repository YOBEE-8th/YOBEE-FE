package com.ssafy.data.remote.mappers

import com.ssafy.data.remote.datasource.user.dto.ExpResponseDto
import com.ssafy.data.remote.datasource.user.dto.IncreaseExpResponseDto
import com.ssafy.data.remote.datasource.user.dto.ReviewByDateResponseDto
import com.ssafy.data.remote.datasource.user.dto.ReviewByRecipeResponseDto
import com.ssafy.domain.model.user.ExpDomainModel
import com.ssafy.domain.model.user.IncreaseExpDomainModel
import com.ssafy.domain.model.user.ReviewByDateDomainModel
import com.ssafy.domain.model.user.ReviewByRecipeDomainModel

internal fun ExpResponseDto.toDomainModel() = ExpDomainModel(
    soupExp = this.soupExp.toFloat(),
    sideExp = this.sideExp.toFloat(),
    grilledExp = this.grilledExp.toFloat(),
    noodleExp = this.noodleExp.toFloat(),
    dessertExp = this.dessertExp.toFloat()
)

internal fun IncreaseExpResponseDto.toDomainModel() = IncreaseExpDomainModel(
    soupExp = this.soupExp.toFloat(),
    sideExp = this.sideExp.toFloat(),
    grilledExp = this.grilledExp.toFloat(),
    noodleExp = this.noodleExp.toFloat(),
    dessertExp = this.dessertExp.toFloat(),
    upCategory = this.upCategory,
    upExp = this.upExp
)

internal fun ReviewByRecipeResponseDto.toDomainModel() = ReviewByRecipeDomainModel(
    recipeId = this.recipeId,
    recipeImage = this.recipeImage,
    title = this.title,
    reviewCnt = this.reviewCnt
)

internal fun ReviewByDateResponseDto.toDomainModel() = ReviewByDateDomainModel(
    reviewId = this.reviewId,
    reviewImage = this.reviewImage,
    title = this.title,
    isCompleted = this.isCompleted,
    createdAt = this.createdAt
)