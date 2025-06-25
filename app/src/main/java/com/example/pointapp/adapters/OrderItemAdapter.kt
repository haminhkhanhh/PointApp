package com.example.pointapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.model.OrderItem

class OrderItemAdapter(
    private val items: MutableList<OrderItem>,
    private val onCartChanged: () -> Unit
) : RecyclerView.Adapter<OrderItemAdapter.OrderItemVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_checkout, parent, false)
        return OrderItemVH(view)
    }

    override fun onBindViewHolder(holder: OrderItemVH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class OrderItemVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: OrderItem) {
            itemView.findViewById<TextView>(R.id.tvProductName).text = item.product.name
            itemView.findViewById<TextView>(R.id.tvOptions).text =
                "Đường: ${item.sugar}, Đá: ${item.ice}, Topping: ${
                    if (item.toppings.isEmpty()) "Không"
                    else item.toppings.joinToString { it.name }
                }"
            val price = item.product.price + item.toppings.sumOf { it.price }
            itemView.findViewById<TextView>(R.id.tvPrice).text = "${(price * item.quantity).toInt()}đ"
            val tvQuantity = itemView.findViewById<TextView>(R.id.tvQuantity)
            tvQuantity.text = item.quantity.toString()

            // Sự kiện tăng/giảm số lượng
            itemView.findViewById<Button>(R.id.btnPlus).setOnClickListener {
                items[adapterPosition] = item.copy(quantity = item.quantity + 1)
                notifyItemChanged(adapterPosition)
                onCartChanged()
            }
            itemView.findViewById<Button>(R.id.btnMinus).setOnClickListener {
                if (item.quantity > 1) {
                    items[adapterPosition] = item.copy(quantity = item.quantity - 1)
                    notifyItemChanged(adapterPosition)
                    onCartChanged()
                }
            }

            // Sự kiện xóa món
            itemView.findViewById<Button>(R.id.btnRemove).setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    items.removeAt(pos)
                    notifyItemRemoved(pos)
                    onCartChanged()
                }
            }
        }
    }
}