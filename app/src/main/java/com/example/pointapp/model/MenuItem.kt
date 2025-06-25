package com.example.pointapp.model

sealed class MenuItem {
    data class Header(val title: String) : MenuItem()
    data class Product(val name: String, val price: Int) : MenuItem()
}