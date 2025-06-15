package com.example.pointapp.fragments

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.pointapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

class MembershipFragment : Fragment() {

    private lateinit var imgQRCode: ImageView
    private lateinit var tvPhone: TextView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_membership, container, false)
        imgQRCode = view.findViewById(R.id.imgQRCode)
        tvPhone = view.findViewById(R.id.tvPhone)
        loadPhoneAndGenerateQR()
        return view
    }

    private fun convertPhone84To0(phone: String): String {
        return if (phone.startsWith("+84")) {
            "0" + phone.substring(3)
        } else phone
    }

    private fun loadPhoneAndGenerateQR() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val rawPhone = doc.getString("phone") ?: ""
                val phone = convertPhone84To0(rawPhone)
                tvPhone.text = phone
                if (phone.isNotEmpty()) {
                    val qrBitmap = generateQRCode(phone)
                    imgQRCode.setImageBitmap(qrBitmap)
                }
            }
    }

    private fun generateQRCode(text: String, size: Int = 500): Bitmap? {
        return try {
            val bitMatrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.createBitmap(bitMatrix)
        } catch (_: Exception) {
            null
        }
    }
}