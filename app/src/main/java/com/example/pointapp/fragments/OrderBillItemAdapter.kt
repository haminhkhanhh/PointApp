package com.example.pointapp.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.model.OrderBillItem

class OrderBillItemAdapter(private val list: List<OrderBillItem>)
    : RecyclerView.Adapter<OrderBillItemAdapter.OrderItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill_order_admin, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: OrderBillItem) {
            itemView.findViewById<TextView>(R.id.tvProductName).text = item.productName
            itemView.findViewById<TextView>(R.id.tvProductOptions).text =
                "Đường: ${item.sugar}, Đá: ${item.ice}, Topping: ${item.toppingNames.joinToString()}, SL: ${item.quantity}"
            itemView.findViewById<TextView>(R.id.tvProductPrice).text =
                "Tổng: ${(item.unitPrice * item.quantity)}đ"
        }
    }
}