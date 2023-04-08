package com.ssafy.yobee.ui.search.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.ssafy.yobee.R
import com.ssafy.yobee.databinding.ListSearchHistoryItemBinding
import dagger.hilt.android.qualifiers.ApplicationContext

class AutoCompleteAdapter(
    @ApplicationContext context: Context,
    private val itemList: ArrayList<String>,
) :
    ArrayAdapter<String>(context, R.layout.list_search_history_item, itemList) {

    private val inflater = LayoutInflater.from(context)
    private lateinit var keywordClick: (String) -> Unit
    private lateinit var keywordDelete: (Int) -> Unit
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            val binding = ListSearchHistoryItemBinding.inflate(inflater, parent, false)
            view = binding.root
            holder = ViewHolder(binding)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        holder.bind(getItem(position) ?: "")

        return view
    }

    inner class ViewHolder(private val binding: ListSearchHistoryItemBinding) {
        fun bind(item: String) {
            binding.keyword = item
            binding.ibSearchHistoryDelete.setOnClickListener {
                keywordDelete.invoke(getPosition(item))
            }
            binding.tvSearchHistoryKeyword.setOnClickListener {
                keywordClick.invoke(item)
            }
        }
    }

    fun deleteKeyWordClickListener(event: (Int) -> Unit) {
        keywordDelete = event
    }

    fun keywordClickListener(event: (String) -> Unit) {
        keywordClick = event
    }
}
