package com.example.pointapp.model

data class PointHistory(
    val type: String = "",
    val pointChanged: Int = 0,
    val billId: String = "",
    val timestamp: Long = 0L,
    val message: String = ""
)