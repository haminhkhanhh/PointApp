package com.example.pointapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class OrderItem(
    val product: Product,
    val sugar: String,
    val ice: String,
    val toppings: List<Topping>,
    val quantity: Int = 1
) : Parcelable
