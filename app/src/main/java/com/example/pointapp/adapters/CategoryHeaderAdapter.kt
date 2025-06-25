package com.example.pointapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.model.Category

class CategoryHeaderAdapter(
    val categories: List<Category>,
    val onClick: (Category, Int) -> Unit
) : RecyclerView.Adapter<CategoryHeaderAdapter.CategoryViewHolder>() {

    var selectedIndex = 0

    inner class CategoryViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv) {
        init {
            tv.setOnClickListener {
                onClick(categories[adapterPosition], adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val tv = LayoutInflater.from(parent.context).inflate(R.layout.item_category_header, parent, false) as TextView
        return CategoryViewHolder(tv)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val cat = categories[position]
        holder.tv.text = cat.name
        holder.tv.isSelected = (selectedIndex == position)
        holder.tv.setTextColor(if (selectedIndex == position) 0xFFE91E63.toInt() else 0xFF000000.toInt())
    }

    override fun getItemCount() = categories.size

    fun select(pos: Int) {
        val prev = selectedIndex
        selectedIndex = pos
        notifyItemChanged(prev)
        notifyItemChanged(pos)
    }
}

