package com.ssafy.yobee.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.domain.model.recipe.RecipeDomainModel
import com.ssafy.yobee.databinding.ListRecipeItemBinding


class RecipeSearchListAdapter : ListAdapter<RecipeDomainModel, RecipeSearchListAdapter.ViewHolder>(
    RecipeDiffUtil
) {
    private lateinit var addLikeItem: (Int) -> Unit
    private lateinit var recipeClick: (Int, String) -> Unit

    fun addLikeRecipeButtonClickListener(event: (Int) -> Unit) {
        addLikeItem = event
    }

    fun addRecipeClickListener(recipe: (Int, String) -> Unit) {
        recipeClick = recipe
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ListRecipeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ViewHolder(private val binding: ListRecipeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RecipeDomainModel) {
            binding.apply {
                recipe = item
                ivListRecipeItemLike.setOnClickListener {
                    addLikeItem.invoke(item.recipeId)
                    ivListRecipeItemLike.toggle()
                }
                root.setOnClickListener {
                    recipeClick.invoke(item.recipeId, item.title)
                }
            }
        }
    }

    object RecipeDiffUtil : DiffUtil.ItemCallback<RecipeDomainModel>() {
        override fun areItemsTheSame(
            oldItem: RecipeDomainModel,
            newItem: RecipeDomainModel,
        ): Boolean {
            return oldItem.imageUrl == newItem.imageUrl
        }

        override fun areContentsTheSame(
            oldItem: RecipeDomainModel,
            newItem: RecipeDomainModel,
        ): Boolean {
            return oldItem == newItem
        }
    }

}

