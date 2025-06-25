package com.example.pointapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.adapters.ToppingAdapter
import com.example.pointapp.model.Topping
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CustomizeDrinkBottomSheet(
    private val toppingList: List<Topping>,
    val onConfirm: (sugar: String, ice: String, toppings: List<Topping>) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var toppingAdapter: ToppingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_bottomsheet_customize_drink, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Đường
        val rgSugar = view.findViewById<RadioGroup>(R.id.rgSugar)
        // Đá
        val rgIce = view.findViewById<RadioGroup>(R.id.rgIce)

        // Topping
        val rvTopping = view.findViewById<RecyclerView>(R.id.rvTopping)
        toppingAdapter = ToppingAdapter(toppingList)
        rvTopping.adapter = toppingAdapter
        rvTopping.layoutManager = LinearLayoutManager(context)

        // Xác nhận
        view.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            val sugar = when (rgSugar.checkedRadioButtonId) {
                R.id.rbSugar100 -> "100%"
                R.id.rbSugar70  -> "70%"
                R.id.rbSugar50  -> "50%"
                R.id.rbSugar30  -> "30%"
                R.id.rbSugar0   -> "0%"
                else -> "100%"
            }
            val ice = when (rgIce.checkedRadioButtonId) {
                R.id.rbIce100 -> "100%"
                R.id.rbIce70  -> "70%"
                R.id.rbIce50  -> "50%"
                R.id.rbIce30  -> "30%"
                R.id.rbIce0   -> "0%"
                else -> "100%"
            }
            val selectedToppings = toppingAdapter.selectedToppings.toList()
            onConfirm(sugar, ice, selectedToppings)
            dismiss()
        }
    }
}
