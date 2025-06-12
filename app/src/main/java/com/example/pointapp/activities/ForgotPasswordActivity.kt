package com.example.pointapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pointapp.R
import com.example.pointapp.Utils.normalizePhone
import com.example.pointapp.Utils.isValidVietnamesePhoneNumber
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var edtEmailOrPhone: EditText
    private lateinit var btnSend: Button
    private lateinit var tvBackLogin: TextView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        edtEmailOrPhone = findViewById(R.id.edtEmailOrPhone)
        btnSend = findViewById(R.id.btnSend)
        tvBackLogin = findViewById(R.id.tvBackLogin)

        btnSend.setOnClickListener {
            val input = edtEmailOrPhone.text.toString().trim()
            if (input.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email hoặc số điện thoại!", Toast.LENGTH_SHORT).show()
            } else if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                // Nhập email: Gửi email reset password
                auth.sendPasswordResetEmail(input)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Đã gửi email đặt lại mật khẩu!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Không gửi được email. Kiểm tra lại email!", Toast.LENGTH_LONG).show()
                        }
                    }
            } else if (input.matches(Regex("^\\+?[0-9]{9,11}$"))) {
                // Nhập số điện thoại: Chuẩn hóa, tìm email trong Firestore rồi gửi mail reset
                val normPhone = normalizePhone(input)
                db.collection("users")
                    .whereEqualTo("phone", normPhone)
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty) {
                            val email = result.documents[0].getString("email")
                            if (!email.isNullOrEmpty()) {
                                auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this, "Đã gửi email đặt lại mật khẩu cho tài khoản này!", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(this, "Không gửi được email cho tài khoản này!", Toast.LENGTH_LONG).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(this, "Không tìm thấy email ứng với số điện thoại này!", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this, "Không tìm thấy tài khoản với số điện thoại này!", Toast.LENGTH_LONG).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Lỗi truy vấn dữ liệu!", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Vui lòng nhập đúng email hoặc số điện thoại!", Toast.LENGTH_SHORT).show()
            }
        }

        tvBackLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}