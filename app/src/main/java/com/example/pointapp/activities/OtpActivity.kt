package com.example.pointapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pointapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class OtpActivity : AppCompatActivity() {
    private lateinit var edtOtp: EditText
    private lateinit var btnVerify: Button
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private var phone: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        edtOtp = findViewById(R.id.edtOtp)
        btnVerify = findViewById(R.id.btnVerify)
        auth = FirebaseAuth.getInstance()
        verificationId = intent.getStringExtra("verificationId")
        phone = intent.getStringExtra("phone")

        btnVerify.setOnClickListener {
            val code = edtOtp.text.toString().trim()
            if (code.length != 6) {
                edtOtp.error = "Nhập đúng 6 số OTP"
                return@setOnClickListener
            }
            val credential = PhoneAuthProvider.getCredential(verificationId ?: "", code)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, CompleteProfileActivity::class.java)
                intent.putExtra("phone", phone)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Xác thực OTP thất bại: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}