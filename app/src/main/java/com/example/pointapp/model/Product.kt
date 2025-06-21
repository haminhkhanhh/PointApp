package com.example.pointapp.model

data class Product(
    val id: String = "",
    val name: String = "",
    val desc: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val categoryId: String = ""
)

