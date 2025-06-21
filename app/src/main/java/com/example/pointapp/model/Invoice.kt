package com.example.pointapp.model

data class Invoice(
    val user: User,
    val store: Store,
    val invoiceId: String = "",
    val pointEarn: Double = 0.0,
    val price: Int = 0,
    val quantity: Int = 0,
    val rating: Double = 0.0,

)