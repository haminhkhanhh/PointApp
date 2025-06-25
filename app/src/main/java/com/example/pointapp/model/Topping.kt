package com.example.pointapp.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Topping(
    val id: String = "",
    val name: String = "",
    val price: Int = 0
) : Parcelable
