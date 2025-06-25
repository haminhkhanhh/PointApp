package com.example.pointapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.model.Category
import com.example.pointapp.model.Product

class SectionedProductAdapter(
    private val categories: List<Category>,
    private val products: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }

    // Flat list: Pair(isHeader, index or hash)
    private val flatList = mutableListOf<Pair<Boolean, Int>>()
    private val productsByCategory = mutableMapOf<String, List<Product>>()

    init {
        rebuildList()
    }

    // Cập nhật lại list mỗi khi data đổi
    private fun rebuildList() {
        flatList.clear()
        productsByCategory.clear()
        for ((catIdx, cat) in categories.withIndex()) {
            // Lấy danh sách sản phẩm (có thể đã lọc) của category này
            val filtered = products.filter { it.categoryId == cat.id }
            if (filtered.isNotEmpty()) {
                // Chỉ add header nếu có product
                flatList.add(true to catIdx) // header
                productsByCategory[cat.id] = filtered
                for ((prodIdx, _) in filtered.withIndex()) {
                    flatList.add(false to (catIdx to prodIdx).hashCode()) // item
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (flatList[position].first) TYPE_HEADER else TYPE_ITEM

    override fun getItemCount(): Int = flatList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == TYPE_HEADER)
            HeaderVH(LayoutInflater.from(parent.context).inflate(R.layout.item_section_header, parent, false))
        else
            ItemVH(LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val (isHeader, idx) = flatList[position]
        if (isHeader) {
            val cat = categories[idx]
            (holder as HeaderVH).bind(cat)
        } else {
            // idx là hash(catIdx to prodIdx)
            var found: Product? = null
            for ((catIdx, cat) in categories.withIndex()) {
                val filtered = productsByCategory[cat.id] ?: continue
                for ((prodIdx, prod) in filtered.withIndex()) {
                    if ((catIdx to prodIdx).hashCode() == idx) {
                        found = prod
                        break
                    }
                }
                if (found != null) break
            }
            found?.let { (holder as ItemVH).bind(it, onProductClick) }
        }
    }

    // Khi đổi data thì gọi lại rebuildList và notify
    fun updateData(newCategories: List<Category>, newProducts: List<Product>) {
        // Nếu bạn cần dynamic update data:
        // this.categories = newCategories // Nếu muốn đổi categories
        // this.products = newProducts     // Nếu muốn đổi products
        // Ở đây biến là val nên tạo adapter mới vẫn OK (theo flow bạn đang dùng)
        rebuildList()
        notifyDataSetChanged()
    }

    class HeaderVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(cat: Category) {
            itemView.findViewById<TextView>(R.id.tvSectionHeader).text = cat.name
        }
    }

    class ItemVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(prod: Product, onClick: (Product) -> Unit) {
            itemView.findViewById<TextView>(R.id.txtProductName).text = prod.name
            itemView.findViewById<TextView>(R.id.txtProductDesc)?.text = prod.desc
            itemView.findViewById<TextView>(R.id.txtProductPrice).text = "${prod.price.toInt()}đ"

            // Button "Thêm" hoặc "btnAdd"
            itemView.findViewById<Button>(R.id.btnAdd).setOnClickListener {
                onClick(prod)
            }
        }
    }
}