package com.example.pointapp.activities

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

//import java.util.logging.Handler

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fullscreen (ẩn status bar)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        actionBar?.hide()
        setContentView(R.layout.activity_splash)

        val logo = findViewById<ImageView>(R.id.logo)

// 1. Fade in (0.7s)
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration = 700
        fadeIn.fillAfter = true

        // 2. Fade out (0.6s)
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.duration = 600
        fadeOut.fillAfter = true

        // 1. Start fade in ngay khi vào activity
        logo.startAnimation(fadeIn)

        // 2. Sau khi fade in xong + giữ nguyên 1.5s → fade out
        Handler(Looper.getMainLooper()).postDelayed({
            logo.startAnimation(fadeOut)
        }, 700 + 1500) // 0.7s fade in + 1.5s giữ = 2.2s

        // 3. Sau khi fade out xong (tổng 2.2s + 0.6s = 2.8s) → chuyển màn hình khác
        Handler(Looper.getMainLooper()).postDelayed({
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            if (user != null) {
                // Đã đăng nhập, chuyển sang MainActivity (hoặc HomeActivity)
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Chưa đăng nhập, chuyển sang LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 700 + 1500 + 600)
    }
}