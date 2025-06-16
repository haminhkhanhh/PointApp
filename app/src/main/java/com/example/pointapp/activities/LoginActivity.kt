package com.example.pointapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pointapp.R
import com.google.firebase.auth.FirebaseAuth
import com.example.pointapp.Utils
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val edtUsername = findViewById<EditText>(R.id.edtAccount) // email hoặc sđt
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToSignup = findViewById<TextView>(R.id.tvGoToSignup)
        val tvGoToForgotPassword = findViewById<TextView>(R.id.tvGoToForgotPassword)

        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString()
            loginUser(username, password)
        }

        tvGoToSignup.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        tvGoToForgotPassword.setOnClickListener{
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun loginUser(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            if (!isFinishing && !isDestroyed)
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val showToast: (String) -> Unit = { msg ->
            if (!isFinishing && !isDestroyed)
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            // Đăng nhập bằng email
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val uid = user?.uid
                        if (uid == null) {
                            showToast("Không lấy được UID, đăng nhập thất bại!")
                            return@addOnCompleteListener
                        }
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { doc ->
                                if (!isFinishing && !isDestroyed) {
                                    if (doc != null && doc.exists()) {
                                        val role = doc.getString("role") ?: "user"
                                        if (role == "admin") {
                                            startActivity(Intent(this, AdminMainActivity::class.java))
                                        } else {
                                            startActivity(Intent(this, MainActivity::class.java))
                                        }
                                        finish()
                                    } else {
                                        showToast("Không tìm thấy thông tin tài khoản trong hệ thống!")
                                    }
                                }
                            }
                            .addOnFailureListener {
                                showToast("Không thể lấy thông tin người dùng")
                            }
                    } else {
                        showToast("Đăng nhập thất bại: ${task.exception?.message}")
                    }
                }
        } else {
            // Đăng nhập bằng số điện thoại: chuẩn hóa trước khi tìm
            val phone = Utils.normalizePhone(username)
            db.collection("users")
                .whereEqualTo("phone", phone)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val userDoc = querySnapshot.documents[0]
                        val email = userDoc.getString("email")
                        val uid = userDoc.getString("id") ?: userDoc.id
                        if (!email.isNullOrEmpty() && !uid.isNullOrEmpty()) {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        db.collection("users").document(uid).get()
                                            .addOnSuccessListener { doc ->
                                                if (!isFinishing && !isDestroyed) {
                                                    if (doc != null && doc.exists()) {
                                                        val role = doc.getString("role") ?: "user"
                                                        if (role == "admin") {
                                                            startActivity(Intent(this,
                                                                AdminMainActivity::class.java))
                                                        } else {
                                                            startActivity(Intent(this, MainActivity::class.java))
                                                        }
                                                        finish()
                                                    } else {
                                                        showToast("Không tìm thấy thông tin tài khoản trong hệ thống!")
                                                    }
                                                }
                                            }
                                            .addOnFailureListener {
                                                showToast("Không thể lấy thông tin người dùng")
                                            }
                                    } else {
                                        showToast("Đăng nhập thất bại: ${task.exception?.message}")
                                    }
                                }
                        } else {
                            showToast("Không tìm thấy email hoặc UID ứng với số điện thoại này")
                        }
                    } else {
                        showToast("Số điện thoại không tồn tại!")
                    }
                }
                .addOnFailureListener { e ->
                    showToast("Lỗi: ${e.message}")
                }
        }
    }
}