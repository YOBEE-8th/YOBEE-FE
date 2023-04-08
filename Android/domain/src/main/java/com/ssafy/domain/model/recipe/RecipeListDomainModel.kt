package com.ssafy.domain.model.recipe

data class RecipeDomainModel(
    val recipeId: Int,
    val imageUrl: String,
    val title: String,
    var likeCount: Int,
    val isAI: Boolean,
    var isLike: Boolean,
    val difficulty: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RecipeDomainModel) return false
        if (recipeId != other.recipeId) return false
        if (imageUrl != other.imageUrl) return false
        if (title != other.title) return false
        if (likeCount != other.likeCount) return false
        if (isAI != other.isAI) return false
        if (isLike != other.isLike) return false
        if (difficulty != other.difficulty) return false

        return true
    }

    override fun hashCode(): Int {
        var result = recipeId
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + likeCount
        result = 31 * result + isAI.hashCode()
        result = 31 * result + isLike.hashCode()
        result = 31 * result + difficulty
        return result
    }
}
