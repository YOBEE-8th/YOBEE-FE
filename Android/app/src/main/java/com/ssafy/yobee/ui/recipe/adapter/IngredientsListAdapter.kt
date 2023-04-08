package com.ssafy.yobee.ui.recipe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.yobee.databinding.ListIngredientsItemBinding

class IngredientsListAdapter(val context: Context, private val ingredients: List<String>) :
    RecyclerView.Adapter<IngredientsListAdapter.IngredientsListViewHolder>() {

    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    inner class IngredientsListViewHolder(val binding: ListIngredientsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ingredient: String, position: Int) {
            binding.apply {
                tvIngredientsName.text = ingredient

                llIngredients.setOnClickListener {
                    cbIngredients.isChecked = !(cbIngredients.isChecked)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsListViewHolder {
        return IngredientsListViewHolder(
            ListIngredientsItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: IngredientsListViewHolder, position: Int) {
        holder.apply {
            bind(ingredients[position], position)
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }
}