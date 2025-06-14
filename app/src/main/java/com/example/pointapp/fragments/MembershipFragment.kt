package com.example.pointapp.fragments

import android.graphics.Bitmap
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.pointapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.BarcodeEncoder

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MembershipFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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

    private fun loadPhoneAndGenerateQR() {
        val userId = auth.currentUser?.uid ?: return
        // Giả sử collection tên "users", trường phone chứa số điện thoại
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val phone = doc.getString("phone") ?: ""
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
        } catch (e: Exception) {
            null
        }
    }
}