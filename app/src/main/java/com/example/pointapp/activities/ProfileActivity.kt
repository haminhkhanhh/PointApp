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
    private var userPhone: String? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userId = FirebaseAuth.getInstance().currentUser?.uid
        val uid = FirebaseAuth.getInstance().currentUser?.uid


        // Lấy thông tin user từ Firestore
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId!!)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        userPhone = doc.getString("phone") ?: ""
                        // Có thể lấy thêm tên, email, v.v. ở đây
                    }
                }
        }

        // Đóng profile
        findViewById<ImageView>(R.id.ivClose).setOnClickListener {
            finish()
        }

        // Mở lịch sử giao dịch (ví dụ dùng userPhone)
        findViewById<LinearLayout>(R.id.layoutAccountHistory).setOnClickListener {
            val intent = Intent(this, BillHistoryActivity::class.java)
            FirebaseFirestore.getInstance().collection("users")
                .document(uid.toString()).get().addOnSuccessListener { docUser ->
                    val currentUserPhone = docUser.getString("phone") ?: ""
                    intent.putExtra("customerPhone", currentUserPhone)
                    startActivity(intent)
                }
        }

        // Mở phần thưởng
        findViewById<LinearLayout>(R.id.layoutRewards).setOnClickListener {
            val intent = Intent(this, RewardsActivity::class.java)
            FirebaseFirestore.getInstance().collection("users")
                .document(uid.toString()).get().addOnSuccessListener { docUser ->
                    val currentUserPhone = docUser.getString("phone") ?: ""
                    intent.putExtra("customerPhone", currentUserPhone)
                    startActivity(intent)
                }
        }

        // Mở trang cá nhân
        findViewById<LinearLayout>(R.id.layoutPersonal).setOnClickListener {
            userId?.let { uid ->
                val intent = Intent(this, PersonalActivity::class.java)
                intent.putExtra("userId", uid)
                startActivity(intent)
            }
        }

        // Mở cài đặt
        findViewById<LinearLayout>(R.id.layoutSetting).setOnClickListener {
            // startActivity(Intent(this, SettingActivity::class.java))
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
