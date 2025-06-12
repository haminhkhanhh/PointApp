package com.example.pointapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pointapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.pointapp.Utils

class LoginActivity : AppCompatActivity() {
    private lateinit var edtAccount: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnResendVerify: Button
    private lateinit var tvGoToSignup: TextView
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseDatabase.getInstance().reference

    private var lastLoginEmail: String? = null // lưu email nếu cần gửi lại xác thực

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtAccount = findViewById(R.id.edtAccount)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnResendVerify = findViewById(R.id.btnResendVerify)
        tvGoToSignup = findViewById(R.id.tvGoToSignup)
        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            val account = edtAccount.text.toString().trim()
            val password = edtPassword.text.toString()
            if (account.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (Patterns.EMAIL_ADDRESS.matcher(account).matches()) {
                signIn(account, password)
            } else {
                // Đăng nhập bằng số điện thoại: tra email từ database
                val inputPhone = Utils.normalizePhone(account)
                db.child("customers").get().addOnSuccessListener { snapshot ->
                    var foundEmail: String? = null
                    for (userSnap in snapshot.children) {
                        val dbPhone = userSnap.child("phone").getValue(String::class.java)?.let { Utils.normalizePhone(it) }
                        val email = userSnap.child("email").getValue(String::class.java)
                        if (dbPhone == inputPhone && !email.isNullOrEmpty()) {
                            foundEmail = email
                            break
                        }
                    }
                    if (foundEmail != null) {
                        signIn(foundEmail, password)
                    } else {
                        Toast.makeText(this, "Số điện thoại không tồn tại!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnResendVerify.setOnClickListener {
            val user = auth.currentUser
            val email = lastLoginEmail
            if (user != null && email != null && !user.isEmailVerified) {
                user.sendEmailVerification()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Đã gửi lại email xác thực! Vui lòng kiểm tra mail (kể cả mục Spam).", Toast.LENGTH_LONG).show()
                        auth.signOut()
                        btnResendVerify.visibility = Button.GONE
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gửi lại xác thực lỗi: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Không thể gửi lại xác thực. Hãy đăng nhập lại.", Toast.LENGTH_LONG).show()
            }
        }

        tvGoToSignup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.reload()?.addOnSuccessListener {
                        if (user.isEmailVerified) {
                            checkProfileAndGo(user.uid)
                        } else {
                            lastLoginEmail = email
                            btnResendVerify.visibility = Button.VISIBLE
                            Toast.makeText(
                                this,
                                "Bạn cần xác thực email trước! Nếu chưa nhận được mail xác thực, hãy bấm gửi lại.",
                                Toast.LENGTH_LONG
                            ).show()
                            // KHÔNG signOut ở đây, để user còn gửi lại xác thực
                        }
                    }
                } else {
                    Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkProfileAndGo(uid: String) {
        db.child("customers").child(uid).get().addOnSuccessListener { snapshot ->
            val firstName = snapshot.child("firstName").getValue(String::class.java)
            val lastName = snapshot.child("lastName").getValue(String::class.java)
            val email = snapshot.child("email").getValue(String::class.java)
            val phone = snapshot.child("phone").getValue(String::class.java)
            val birthDay = snapshot.child("birthDay").getValue(Int::class.java)
            val birthMonth = snapshot.child("birthMonth").getValue(Int::class.java)
            val birthYear = snapshot.child("birthYear").getValue(Int::class.java)
            val city = snapshot.child("city").getValue(String::class.java)
            val gender = snapshot.child("gender").getValue(String::class.java)

            val profileOk = !firstName.isNullOrEmpty()
                    && !lastName.isNullOrEmpty()
                    && !email.isNullOrEmpty()
                    && !phone.isNullOrEmpty()
                    && birthDay != null && birthMonth != null && birthYear != null
                    && !city.isNullOrEmpty()
                    && !gender.isNullOrEmpty()

            if (profileOk) {
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            } else {
                startActivity(Intent(this, CompleteProfileActivity::class.java))
                finish()
            }
        }.addOnFailureListener {
            startActivity(Intent(this, CompleteProfileActivity::class.java))
            finish()
        }
    }
}