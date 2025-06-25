package com.example.pointapp.model

data class Bill(
    val billId: String = "",
    val customerPhone: String = "",
    val discount: Double = 0.0,
    val timestamp: Long = 0,
    val total: Double = 0.0,
    val voucherCode: String = ""
)

