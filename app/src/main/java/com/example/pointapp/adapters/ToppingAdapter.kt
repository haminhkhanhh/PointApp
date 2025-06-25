package com.example.pointapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.model.Topping

class ToppingAdapter(
    private val toppings: List<Topping>
) : RecyclerView.Adapter<ToppingAdapter.ToppingVH>() {

    val selectedToppings = mutableSetOf<Topping>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToppingVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_topping, parent, false)
        return ToppingVH(view)
    }

    override fun getItemCount() = toppings.size

    override fun onBindViewHolder(holder: ToppingVH, position: Int) {
        holder.bind(toppings[position])
    }

    inner class ToppingVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(topping: Topping) {
            val cb = itemView.findViewById<CheckBox>(R.id.cbTopping)
            val tvPrice = itemView.findViewById<TextView>(R.id.tvPrice)
            cb.text = topping.name
            tvPrice.text = "+${topping.price}đ"
            cb.setOnCheckedChangeListener(null)
            cb.isChecked = selectedToppings.contains(topping)
            cb.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedToppings.add(topping)
                else selectedToppings.remove(topping)
            }
        }
    }
}