package com.ssafy.yobee.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.common.SingleLiveEvent
import com.ssafy.domain.usecase.recipe.ChangeRecipeLikeStatusUseCase
import com.ssafy.domain.usecase.recipe.GetRecipeDetailUseCase
import com.ssafy.domain.usecase.recipe.GetRecipeIngredientUseCase
import com.ssafy.domain.usecase.recipe.GetRecipeReviewUseCase
import com.ssafy.domain.usecase.review.DeleteReviewUseCase
import com.ssafy.domain.usecase.review.UpdateReviewLikeUseCase
import com.ssafy.domain.usecase.video.VideoUseCase
import com.ssafy.domain.utils.ViewState
import com.ssafy.yobee.ui.recipe.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val videoUseCase: VideoUseCase,
    private val getRecipeIngredientUseCase: GetRecipeIngredientUseCase,
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val getRecipeReviewUseCase: GetRecipeReviewUseCase,
    private val updateReviewLikeUseCase: UpdateReviewLikeUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase,
    private val addLikeRecipeUseCase: ChangeRecipeLikeStatusUseCase,
) : ViewModel() {

    private val _height = MutableLiveData<Int>()
    val height: LiveData<Int>
        get() = _height

    private val _ingredientsHeight = MutableLiveData<Int>()
    val ingredientsHeight: LiveData<Int> get() = _ingredientsHeight

    private val _reviewsHeight = MutableLiveData<Int>()
    val reviewsHeight: LiveData<Int> get() = _reviewsHeight

    private val _youtubeHeight = MutableLiveData<Int>()
    val youtubeHeight: LiveData<Int> get() = _youtubeHeight

    private val _recipeId = SingleLiveEvent<Int>()
    val recipeId: LiveData<Int>
        get() = _recipeId


    fun setIngredientsHeight(height: Int) {
        if (ingredientsHeight.value == null) {
            _ingredientsHeight.value = height
        }
    }

    fun setReviewsHeight(height: Int) {
        if (reviewsHeight.value == null) {
            _reviewsHeight.value = height
        }
    }

    fun setYoutubeHeight(height: Int) {
        if (youtubeHeight.value == null) {
            _youtubeHeight.value = height
        }
    }

    fun setRecipeId(id: Int) {
        _recipeId.postValue(id)
    }

    private val _recipeTitle = SingleLiveEvent<String>()
    val recipeTitle: LiveData<String>
        get() = _recipeTitle

    fun setRecipeTitle(title: String) {
        _recipeTitle.postValue(title)
    }

    private val _recipeImg = SingleLiveEvent<String>()
    val recipeImg: LiveData<String>
        get() = _recipeImg

    fun setRecipeImage(image: String) {
        _recipeImg.postValue(image)
    }

    private val _videoLiveData = SingleLiveEvent<ViewState<VideoModel>>()
    val videoLiveData: LiveData<ViewState<VideoModel>>
        get() = _videoLiveData

    fun getVideos(part: String, maxResults: Int, q: String, key: String) = viewModelScope.launch {
        _videoLiveData.postValue(ViewState.Loading())

        when (val result = videoUseCase(part, maxResults, q, key)) {
            is ViewState.Success -> {
                _videoLiveData.postValue(
                    ViewState.Success(
                        result.message,
                        result.value!!.toVideoModel()
                    )
                )
            }
            is ViewState.Error -> {
                _videoLiveData.postValue(ViewState.Error(result.message))
            }
            is ViewState.Loading -> {}
        }
    }

    private val _ingredientLiveData = SingleLiveEvent<ViewState<IngredientsModel>>()
    val ingredientLiveData: LiveData<ViewState<IngredientsModel>>
        get() = _ingredientLiveData

    fun getRecipeIngredient(recipeId: Int) = viewModelScope.launch {
        _ingredientLiveData.postValue(ViewState.Loading())

        when (val result = getRecipeIngredientUseCase(recipeId)) {
            is ViewState.Success -> {
                _ingredientLiveData.postValue(
                    ViewState.Success(
                        result.message,
                        result.value!!.toIngredientsModel()
                    )
                )
            }
            is ViewState.Error -> {
                _ingredientLiveData.postValue(ViewState.Error(result.message))
            }
            is ViewState.Loading -> {}
        }
    }

    private val _recipeDetailLiveData = SingleLiveEvent<ViewState<RecipeDetailModel>>()
    val recipeDetailLiveData: LiveData<ViewState<RecipeDetailModel>>
        get() = _recipeDetailLiveData

    fun getRecipeDetail(recipeId: Int) = viewModelScope.launch {
        _recipeDetailLiveData.postValue(ViewState.Loading())

        when (val result = getRecipeDetailUseCase(recipeId)) {
            is ViewState.Success -> {
                _recipeDetailLiveData.postValue(
                    ViewState.Success(
                        result.message,
                        result.value!!.toRecipeDetailModel()
                    )
                )
            }
            is ViewState.Error -> {
                _recipeDetailLiveData.postValue(ViewState.Error(result.message))
            }
            is ViewState.Loading -> {}
        }
    }

    private val _recipeReviewLiveData = SingleLiveEvent<ViewState<List<ReviewModel>>>()
    val recipeReviewLiveData: LiveData<ViewState<List<ReviewModel>>>
        get() = _recipeReviewLiveData

    fun getRecipeReview(recipeId: Int) = viewModelScope.launch {
        _recipeReviewLiveData.postValue(ViewState.Loading())

        when (val result = getRecipeReviewUseCase(recipeId)) {
            is ViewState.Success -> {
                val reviewModelList = arrayListOf<ReviewModel>()
                result.value!!.forEach { recipeReviewDomainModel ->
                    reviewModelList.add(recipeReviewDomainModel.toReviewModel())
                }

                _recipeReviewLiveData.postValue(ViewState.Success(result.message, reviewModelList))
            }
            is ViewState.Error -> {
                _recipeReviewLiveData.postValue(ViewState.Error(result.message))
            }
            is ViewState.Loading -> {}
        }
    }

    private val _updateReviewLikeLiveData = SingleLiveEvent<ViewState<Any>>()
    val updateReviewLikeLiveData: LiveData<ViewState<Any>>
        get() = _updateReviewLikeLiveData

    fun updateReviewLike(reviewId: Int) = viewModelScope.launch {

        when (val result = updateReviewLikeUseCase(reviewId)) {
            is ViewState.Success -> {
                _updateReviewLikeLiveData.postValue(ViewState.Success(result.message, result.value))
            }
            is ViewState.Error -> {
                _recipeDetailLiveData.postValue(ViewState.Error(result.message))
            }
            is ViewState.Loading -> {}
        }
    }

    private val _deleteReviewLiveData = SingleLiveEvent<ViewState<Any>>()
    val deleteReviewLiveData: LiveData<ViewState<Any>>
        get() = _deleteReviewLiveData

    fun deleteReview(reviewId: Int) = viewModelScope.launch {
        when (val result = deleteReviewUseCase(reviewId)) {
            is ViewState.Success -> {
                _deleteReviewLiveData.postValue(ViewState.Success(result.message, result.value))
            }
            is ViewState.Error -> {
                _deleteReviewLiveData.postValue(ViewState.Error(result.message))
            }
            is ViewState.Loading -> {}
        }
    }

    private val _addLikeRecipeLiveData = SingleLiveEvent<ViewState<Any>>()
    val addLikeRecipeLiveData: LiveData<ViewState<Any>>
        get() = _addLikeRecipeLiveData

    fun addLikeRecipe(recipeId: Int) = viewModelScope.launch {

        when (val result = addLikeRecipeUseCase(recipeId)) {
            is ViewState.Success -> {
                _addLikeRecipeLiveData.postValue(ViewState.Success(result.message, result.value))
            }
            is ViewState.Error -> {
                _addLikeRecipeLiveData.postValue(ViewState.Error(result.message))
            }
            is ViewState.Loading -> {}
        }
    }
}