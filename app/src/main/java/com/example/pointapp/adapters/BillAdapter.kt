package com.example.pointapp.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.pointapp.R
import com.example.pointapp.model.Bill

class BillAdapter(private var bills: List<Bill>) :
    RecyclerView.Adapter<BillAdapter.BillViewHolder>() {

    inner class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvId: TextView = itemView.findViewById(R.id.tvBillId)
        val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bill, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill = bills[position]
        holder.tvId.text = "Mã hóa đơn: ${bill.billId}"
        holder.tvTotal.text = "Tổng tiền: ${bill.total.toInt()}₫"
        holder.tvTime.text = "Thời gian: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date(bill.timestamp))}"
    }

    override fun getItemCount() = bills.size

    fun updateBills(newBills: List<Bill>) {
        bills = newBills
        notifyDataSetChanged()
    }
}