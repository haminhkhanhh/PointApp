package com.example.pointapp.model

data class OrderBillItem(
    val productName: String = "",
    val sugar: String = "",
    val ice: String = "",
    val toppingNames: List<String> = emptyList(),
    val quantity: Int = 1,
    val unitPrice: Int = 0
)
