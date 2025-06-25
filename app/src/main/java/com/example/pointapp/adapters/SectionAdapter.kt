package com.example.pointapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.model.Product

sealed class SectionItem {
    data class Header(val title: String) : SectionItem()
    data class ProductItem(val product: Product) : SectionItem()
}

class SectionAdapter(
    private val items: List<SectionItem>,
    private val onAddClick: (Product) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SectionItem.Header -> TYPE_HEADER
            is SectionItem.ProductItem -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_section_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false)
            ItemViewHolder(view, onAddClick)
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is SectionItem.Header -> (holder as HeaderViewHolder).bind(item)
            is SectionItem.ProductItem -> (holder as ItemViewHolder).bind(item.product)
        }
    }

    fun getSectionTitle(position: Int): String? {
        for (i in position downTo 0) {
            if (items[i] is SectionItem.Header) {
                return (items[i] as SectionItem.Header).title
            }
        }
        return null
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(header: SectionItem.Header) {
            itemView.findViewById<TextView>(R.id.txtHeaderTitle).text = header.title
        }
    }

    class ItemViewHolder(
        view: View,
        private val onAddClick: (Product) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        fun bind(product: Product) {
            itemView.findViewById<TextView>(R.id.txtProductName).text = product.name
            itemView.findViewById<TextView>(R.id.txtProductPrice).text = "${product.price.toInt()}đ"
            itemView.setOnClickListener { onAddClick(product) }
        }
    }
}