package com.example.pointapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pointapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val edtConfirmPassword = findViewById<EditText>(R.id.edtConfirmPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val tvGoToLogin = findViewById<TextView>(R.id.tvGoToLogin)

        btnSignUp.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString()
            val confirmPassword = edtConfirmPassword.text.toString()

            val errorMsg = validatePassword(password, confirmPassword)
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                // Sau khi gửi email xác thực, lưu thông tin user với role vào Firestore
                                val uid = user.uid
                                val userMap = hashMapOf(
                                    "email" to email,
                                    "role" to "user" // hoặc "admin" nếu bạn muốn tạo tài khoản admin
                                )
                                db.collection("users").document(uid)
                                    .set(userMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Đăng ký thành công! Vui lòng kiểm tra email để xác thực.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        // Chờ xác thực email rồi mới cho phép chuyển tiếp
                                        val intent = Intent(this, EmailVerificationActivity::class.java)
                                        intent.putExtra("email", email)
                                        intent.putExtra("uid", user.uid)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this,
                                            "Đăng ký thành công nhưng lưu thông tin thất bại: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Lỗi gửi email xác thực: ${verifyTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Đăng ký thất bại: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        tvGoToLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    // Hàm kiểm tra password mạnh và xác nhận khớp
    private fun validatePassword(password: String, confirmPassword: String): String? {
        if (password.length < 8)
            return "Mật khẩu phải có ít nhất 8 ký tự"
        if (!password.any { it.isUpperCase() })
            return "Mật khẩu phải có ít nhất 1 chữ hoa"
        if (!password.any { it.isLowerCase() })
            return "Mật khẩu phải có ít nhất 1 chữ thường"
        if (!password.any { it.isDigit() })
            return "Mật khẩu phải có ít nhất 1 số"
        if (!password.any { !it.isLetterOrDigit() })
            return "Mật khẩu phải có ít nhất 1 ký tự đặc biệt"
        if (password != confirmPassword)
            return "Mật khẩu nhập lại không trùng khớp"
        return null // Mật khẩu hợp lệ
    }
}