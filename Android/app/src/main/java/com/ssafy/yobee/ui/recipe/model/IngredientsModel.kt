package com.ssafy.yobee.ui.recipe.model

import com.ssafy.domain.model.recipe.RecipeIngredientDomainModel

data class IngredientsModel(
    val ingredients: List<String>,
)

fun RecipeIngredientDomainModel.toIngredientsModel() = IngredientsModel(
    ingredients = this.ingredient
)