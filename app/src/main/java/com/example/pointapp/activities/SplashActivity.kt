package com.example.pointapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.pointapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

//import java.util.logging.Handler

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        actionBar?.hide()
        setContentView(R.layout.activity_splash)

        val logo = findViewById<ImageView>(R.id.logo)

        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 700
        fadeIn.fillAfter = true

        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.duration = 600
        fadeOut.fillAfter = true

        logo.startAnimation(fadeIn)

        Handler(Looper.getMainLooper()).postDelayed({
            logo.startAnimation(fadeOut)
        }, 700 + 1500)

        Handler(Looper.getMainLooper()).postDelayed({
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // Đã đăng nhập, lấy role từ Firestore
                val uid = user.uid
                FirebaseFirestore.getInstance().collection("users").document(uid).get()
                    .addOnSuccessListener { doc ->
                        val role = doc.getString("role") ?: "user"
                        if (role == "admin") {
                            startActivity(Intent(this, AdminMainActivity::class.java))
                        } else {
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                        finish()
                    }
                    .addOnFailureListener {
                        // Nếu lấy role lỗi, cho vào MainActivity mặc định
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
            } else {
                // Chưa đăng nhập, chuyển sang LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }, 700 + 1500 + 600)
    }
}