package com.example.pointapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pointapp.R
import android.widget.*
import com.example.pointapp.model.Store
import com.google.firebase.firestore.FirebaseFirestore

class AdminEditStoreActivity : AppCompatActivity() {
    private lateinit var edtStoreName: EditText
    private lateinit var edtStoreAddress: EditText
    private lateinit var edtStorePhone: EditText
    private lateinit var edtLatitude: EditText
    private lateinit var edtLongitude: EditText
    private lateinit var btnSave: Button

    private var storeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_edit_store)

        storeId = intent.getStringExtra("storeId")
        if (storeId.isNullOrEmpty()) {
            Toast.makeText(this, "Thiếu storeId", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        edtStoreName = findViewById(R.id.edtStoreName)
        edtStoreAddress = findViewById(R.id.edtStoreAddress)
        edtStorePhone = findViewById(R.id.edtStorePhone)
        edtLatitude = findViewById(R.id.edtLatitude)
        edtLongitude = findViewById(R.id.edtLongitude)
        btnSave = findViewById(R.id.btnSave)

        loadStoreInfo()

        btnSave.setOnClickListener { saveStoreInfo() }
    }

    private fun loadStoreInfo() {
        FirebaseFirestore.getInstance().collection("stores")
            .document(storeId!!)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val store = doc.toObject(Store::class.java)
                    edtStoreName.setText(store?.storeName ?: "")
                    edtStoreAddress.setText(store?.address ?: "")
                    edtStorePhone.setText(store?.storePhone ?: "")
                    edtLatitude.setText(store?.latitude?.toString() ?: "")
                    edtLongitude.setText(store?.longitude?.toString() ?: "")
                } else {
                    Toast.makeText(this, "Không tìm thấy cửa hàng!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveStoreInfo() {
        val storeName = edtStoreName.text.toString().trim()
        val address = edtStoreAddress.text.toString().trim()
        val storePhone = edtStorePhone.text.toString().trim()
        val latitude = edtLatitude.text.toString().toDoubleOrNull() ?: 0.0
        val longitude = edtLongitude.text.toString().toDoubleOrNull() ?: 0.0

        if (storeName.isEmpty() || address.isEmpty() || storePhone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
            return
        }

        val store = Store(
            storeId = storeId!!,
            storeName = storeName,
            latitude = latitude,
            longitude = longitude,
            address = address,
            storePhone = storePhone
        )

        FirebaseFirestore.getInstance().collection("stores")
            .document(storeId!!)
            .set(store)
            .addOnSuccessListener {
                Toast.makeText(this, "Đã lưu thông tin cửa hàng!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lưu thất bại!", Toast.LENGTH_SHORT).show()
            }
    }
}