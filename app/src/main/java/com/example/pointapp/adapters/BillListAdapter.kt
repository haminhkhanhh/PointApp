package com.example.pointapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.model.BillAdmin
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BillListAdapter(
    private val list: List<BillAdmin>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<BillListAdapter.BillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bill_admin, parent, false)
        return BillViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        holder.bind(list[position], onClick)
    }

    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvBillId = itemView.findViewById<TextView>(R.id.tvBillId)
        private val tvBillTime = itemView.findViewById<TextView>(R.id.tvBillTime)
        private val tvBillTotal = itemView.findViewById<TextView>(R.id.tvBillTotal)

        fun bind(item: BillAdmin, onClick: (String) -> Unit) {
            tvBillId.text = "ID: ${item.billId}"
            tvBillTime.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(item.timestamp))
            tvBillTotal.text = "${item.total.toInt()}đ"
            itemView.setOnClickListener { onClick(item.billId) }
        }
    }
}