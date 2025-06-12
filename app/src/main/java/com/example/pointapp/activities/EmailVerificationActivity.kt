package com.example.pointapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pointapp.R
import com.google.firebase.auth.FirebaseAuth

class EmailVerificationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var tvStatus: TextView
    private lateinit var btnCheck: Button
    private lateinit var btnResend: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verification)

        auth = FirebaseAuth.getInstance()
        tvStatus = findViewById(R.id.tvStatus)
        btnCheck = findViewById(R.id.btnCheckVerification)
        btnResend = findViewById(R.id.btnResendVerification)

        val email = intent.getStringExtra("email") ?: ""
        val uid = intent.getStringExtra("uid") ?: ""

        tvStatus.text = "Vui lòng kiểm tra email:\n$email\nvà xác thực tài khoản để tiếp tục."

        // Nút kiểm tra trạng thái xác thực
        btnCheck.setOnClickListener {
            auth.currentUser?.reload()?.addOnSuccessListener {
                if (auth.currentUser?.isEmailVerified == true) {
                    Toast.makeText(this, "Xác thực email thành công!", Toast.LENGTH_SHORT).show()
                    // Cho phép chuyển tiếp tới CompleteProfileActivity
                    val intent = Intent(this, CompleteProfileActivity::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("uid", uid)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Bạn chưa xác thực email!", Toast.LENGTH_SHORT).show()
                }
            }?.addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi kiểm tra xác thực: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Nút gửi lại email xác thực
        btnResend.setOnClickListener {
            auth.currentUser?.sendEmailVerification()
                ?.addOnSuccessListener {
                    Toast.makeText(this, "Đã gửi lại email xác thực!", Toast.LENGTH_SHORT).show()
                }
                ?.addOnFailureListener { e ->
                    Toast.makeText(this, "Lỗi gửi lại xác thực: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}