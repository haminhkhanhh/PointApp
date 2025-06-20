package com.example.pointapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.model.Product

class ProductAdapter(
    private var products: List<Product>,
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(), Filterable {

    private var productsFiltered = products

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = productsFiltered.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productsFiltered[position])
    }

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(product: Product) {
            itemView.findViewById<TextView>(R.id.tvProductName).text = product.name
            itemView.findViewById<TextView>(R.id.tvProductDesc).text = product.desc
            itemView.findViewById<TextView>(R.id.tvProductPrice).text = "${product.price.toInt()}đ"
            // Load ảnh nếu có (sử dụng Glide/Picasso):
            // Glide.with(itemView).load(product.imageUrl).into(itemView.findViewById(R.id.imgProduct))
            itemView.findViewById<ImageButton>(R.id.btnAddToCart).setOnClickListener {
                onAddToCart(product)
            }
        }
    }

    fun updateList(newList: List<Product>) {
        this.products = newList
        this.productsFiltered = newList
        notifyDataSetChanged()
    }

    // Filter cho SearchView
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.trim()?.lowercase() ?: ""
                val result = if (query.isEmpty()) {
                    products
                } else {
                    products.filter {
                        it.name.lowercase().contains(query) ||
                                it.desc.lowercase().contains(query)
                    }
                }
                return FilterResults().apply { values = result }
            }
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                productsFiltered = results?.values as List<Product>
                notifyDataSetChanged()
            }
        }
    }
}
