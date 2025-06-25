package com.example.pointapp.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.model.Voucher

class VoucherAdapter(private val list: List<Voucher>) :
    RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_voucher_card, parent, false)
        return VoucherViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        holder.bind(list[position])
    }

    class VoucherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvVoucherTitle)
        private val tvValue = itemView.findViewById<TextView>(R.id.tvVoucherValue)
        private val tvExpire = itemView.findViewById<TextView>(R.id.tvVoucherExpire)

        fun bind(voucher: Voucher) {
            tvTitle.text = "Voucher: ${voucher.code}"
            tvValue.text = "Giảm ${voucher.value}đ"
            tvExpire.text = "HSD: ${voucher.expireDate}"
            // Nếu muốn đánh dấu đã dùng thì đổi màu hoặc gạch ngang ở đây
            if (voucher.isUsed) {
                tvTitle.setTextColor(itemView.context.getColor(android.R.color.darker_gray))
                tvValue.paintFlags = tvValue.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }
}