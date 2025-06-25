package com.example.pointapp.adapters

import com.example.pointapp.model.PointHistory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.model.BillHistory
import java.text.SimpleDateFormat
import java.util.*

class BillHistoryAdapter(private val list: List<BillHistory>) :
    RecyclerView.Adapter<BillHistoryAdapter.BillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bill_history, parent, false)
        return BillViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        holder.bind(list[position])
    }

    class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvBillId = itemView.findViewById<TextView>(R.id.tvBillId)
        private val tvBillDate = itemView.findViewById<TextView>(R.id.tvBillDate)
        private val tvBillTotal = itemView.findViewById<TextView>(R.id.tvBillTotal)
        private val tvBillVoucher = itemView.findViewById<TextView>(R.id.tvBillVoucher)

        fun bind(item: BillHistory) {
            tvBillId.text = "ID: ${item.billId}"
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            tvBillDate.text = sdf.format(Date(item.timestamp))
            tvBillTotal.text = "Tổng: ${item.total}đ"
            tvBillVoucher.text = if (item.voucherCode.isNotEmpty())
                "Voucher: ${item.voucherCode}"
            else "Voucher: Không sử dụng"
        }
    }
}