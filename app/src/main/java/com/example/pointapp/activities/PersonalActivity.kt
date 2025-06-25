package com.example.pointapp.activities

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pointapp.R
import com.google.firebase.firestore.FirebaseFirestore

class PersonalActivity : AppCompatActivity() {
    private lateinit var edtFirstName: EditText
    private lateinit var edtLastName: EditText
    private lateinit var edtDay: EditText
    private lateinit var edtMonth: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtCity: EditText
    private lateinit var spGender: Spinner
    private val genderOptions = arrayOf("Nam", "Nữ", "Khác")
    private lateinit var btnUpdate: Button

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_edit_user)

        userId = intent.getStringExtra("userId")
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "Thiếu userId", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Ánh xạ view
        edtFirstName = findViewById(R.id.edtFirstName)
        edtLastName = findViewById(R.id.edtLastName)
        edtDay = findViewById(R.id.edtDay)
        edtDay.isEnabled = false
        edtMonth = findViewById(R.id.edtMonth)
        edtMonth.isEnabled = false
        edtPhone = findViewById(R.id.edtPhone)
        edtPhone.isEnabled = false
        edtEmail = findViewById(R.id.edtEmail)
        edtEmail.isEnabled = false
        edtCity = findViewById(R.id.edtCity)
        spGender = findViewById(R.id.spGender)
        btnUpdate = findViewById(R.id.btnUpdate)

        findViewById<ImageView?>(R.id.btnClose)?.setOnClickListener { finish() }

        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGender.adapter = genderAdapter

        loadUserInfo()

        btnUpdate.setOnClickListener { updateUserInfo() }
    }

    private fun loadUserInfo() {
        userId?.let { uid ->
            FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        edtFirstName.setText(doc.getString("firstName") ?: "")
                        edtLastName.setText(doc.getString("lastName") ?: "")
                        edtDay.setText(doc.getLong("birthDay")?.toString() ?: "")
                        edtMonth.setText(doc.getLong("birthMonth")?.toString() ?: "")
                        edtPhone.setText(doc.getString("phone") ?: "")
                        edtEmail.setText(doc.getString("email") ?: "")
                        edtCity.setText(doc.getString("city") ?: "")
                        val gender = doc.getString("gender") ?: ""
                        val pos = genderOptions.indexOfFirst { it.equals(gender, ignoreCase = true) }
                        if (pos >= 0) spGender.setSelection(pos)
                    } else {
                        Toast.makeText(this, "Không tìm thấy user!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateUserInfo() {
        val firstName = edtFirstName.text.toString().trim()
        val lastName = edtLastName.text.toString().trim()
        val day = edtDay.text.toString().trim().toIntOrNull()
        val month = edtMonth.text.toString().trim().toIntOrNull()
        val phone = edtPhone.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val city = edtCity.text.toString().trim()
        val gender = spGender.selectedItem.toString()

        if (firstName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên và số điện thoại!", Toast.LENGTH_SHORT).show()
            return
        }

        val userMap = mutableMapOf<String, Any>(
            "firstName" to firstName,
            "lastName" to lastName,
            "phone" to phone,
            "email" to email,
            "city" to city,
            "gender" to gender
        )
        if (day != null) userMap["day"] = day
        if (month != null) userMap["month"] = month

        userId?.let { uid ->
            FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .update(userMap)
                .addOnSuccessListener {
                    AlertDialog.Builder(this)
                        .setTitle("Thành công")
                        .setMessage("Cập nhật thông tin thành viên thành công!")
                        .setPositiveButton("OK") { _, _ -> finish() }
                        .show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show()
                }
        }
    }
}