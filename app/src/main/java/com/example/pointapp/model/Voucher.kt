package com.example.pointapp.model

data class Voucher(
    val code: String = "",
    val value: Long = 0,
    val expireDate: Long = 0, // Timestamp (millis)
    val isUsed: Boolean = false,
    val phoneNumber: String = ""
)
