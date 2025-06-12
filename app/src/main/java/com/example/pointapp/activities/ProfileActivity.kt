package com.example.pointapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.pointapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Đóng profile
        findViewById<ImageView>(R.id.ivClose).setOnClickListener {
            finish()
        }

        // Mở lịch sử giao dịch
        findViewById<LinearLayout>(R.id.layoutAccountHistory).setOnClickListener {
//            startActivity(Intent(this, AccountHistoryActivity::class.java))
        }

        // Mở phần thưởng
        findViewById<LinearLayout>(R.id.layoutRewards).setOnClickListener {
//            startActivity(Intent(this, RewardsActivity::class.java))
        }

        // Mở trang cá nhân
        findViewById<LinearLayout>(R.id.layoutPersonal).setOnClickListener {
//            startActivity(Intent(this, PersonalActivity::class.java))
        }

        // Mở cài đặt
        findViewById<LinearLayout>(R.id.layoutSetting).setOnClickListener {
//            startActivity(Intent(this, SettingActivity::class.java))
        }

        // Đăng xuất
        findViewById<Button>(R.id.btnSignOut).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất") { dialog, _ ->
                     FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    dialog.dismiss()
                }
                .setNegativeButton("Hủy") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}