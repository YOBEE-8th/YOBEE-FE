package com.ssafy.yobee.ui.recipe_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.recipe.RecipeDomainModel
import com.ssafy.yobee.databinding.ListRecipeItemBinding


class RecipeListAdapter :
    ListAdapter<RecipeDomainModel, RecipeListAdapter.ViewHolder>(RecipeDiffUtil) {
    private lateinit var addLikeRecipe: (Int) -> Unit
    private lateinit var recipeItemClick: (Int, String) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ListRecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ViewHolder(private val binding: ListRecipeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecipeDomainModel) {
            binding.recipe = item
            binding.root.setOnClickListener {
                recipeItemClick.invoke(item.recipeId, item.title)
            }
            binding.ivListRecipeItemLike.setOnClickListener {
                addLikeRecipe.invoke(item.recipeId)
                binding.ivListRecipeItemLike.toggle()
            }
        }
    }

    fun addLikeRecipeButtonClickListener(recipeId: (Int) -> Unit) {
        addLikeRecipe = recipeId
    }

    fun addRecipeItemClickListener(recipe: (Int, String) -> Unit) {
        recipeItemClick = recipe
    }

    object RecipeDiffUtil : DiffUtil.ItemCallback<RecipeDomainModel>() {
        override fun areItemsTheSame(
            oldItem: RecipeDomainModel,
            newItem: RecipeDomainModel,
        ): Boolean {
            return oldItem.recipeId == newItem.recipeId
        }

        override fun areContentsTheSame(
            oldItem: RecipeDomainModel,
            newItem: RecipeDomainModel,
        ): Boolean {
            return oldItem == newItem
        }
    }
}

