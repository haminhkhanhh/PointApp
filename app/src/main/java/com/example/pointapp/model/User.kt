package com.example.pointapp.model

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val birthDay: Int = 0,
    val birthMonth: Int = 0,
    val birthYear: Int = 0,
    val city: String = "",
    val gender: String = "",
    val role: String = ""
)
