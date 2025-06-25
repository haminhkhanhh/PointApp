package com.example.pointapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pointapp.R
import android.content.Intent
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.Utils
import com.example.pointapp.adapters.VoucherAdapter
import com.example.pointapp.model.Voucher
import com.google.firebase.firestore.FirebaseFirestore

class RewardsActivity : AppCompatActivity() {
    private lateinit var tvStarPoint: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvMembershipStatus: TextView
    private lateinit var btnViewHistory: Button
    private lateinit var rvVouchers: RecyclerView
    private lateinit var voucherAdapter: VoucherAdapter
    private val voucherList = mutableListOf<Voucher>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards)

        tvStarPoint = findViewById(R.id.tvStarPoint)
        progressBar = findViewById(R.id.progressBar)
        tvMembershipStatus = findViewById(R.id.tvMembershipStatus)
        btnViewHistory = findViewById(R.id.btnViewHistory)
        rvVouchers = findViewById<RecyclerView>(R.id.rvVouchers)

        // Set up voucher RecyclerView
        voucherAdapter = VoucherAdapter(voucherList)
        rvVouchers.layoutManager = LinearLayoutManager(this)
        rvVouchers.adapter = voucherAdapter

        // Lấy customerPhone truyền qua Intent
        val customerPhone = intent.getStringExtra(Utils.normalizePhone("customerPhone")) ?: return

        // Lấy điểm & membership từ Firestore
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("phone", Utils.normalizePhone(customerPhone))
            .limit(1)
            .get()
            .addOnSuccessListener { snap ->
                val userDoc = snap.documents.firstOrNull()
                val point = userDoc?.getLong("point") ?: 0
                tvStarPoint.text = point.toString()
                progressBar.progress = point.coerceAtMost(500).toInt()

                // Xác định hạng thành viên
                tvMembershipStatus.text = when {
                    point >= 500 -> "PLATINUM MEMBER"
                    point >= 250 -> "GOLD MEMBER"
                    point >= 100 -> "SILVER MEMBER"
                    else -> "MEMBER"
                }
            }

        // Hiển thị voucher còn hiệu lực (chưa dùng, chưa hết hạn)
        FirebaseFirestore.getInstance().collection("vouchers")
            .whereEqualTo("phoneNumber", customerPhone)
            .whereEqualTo("isUsed", false)
            .get()
            .addOnSuccessListener { snapshot ->
                voucherList.clear()
                val now = System.currentTimeMillis()
                for (doc in snapshot.documents) {
                    val voucher = doc.toObject(Voucher::class.java)
                    // Kiểm tra chưa hết hạn
                    if (voucher != null && (voucher.expireDate == 0L || voucher.expireDate > now)) {
                        voucherList.add(voucher)
                    }
                }
                voucherAdapter.notifyDataSetChanged()
            }

        btnViewHistory.setOnClickListener {
            val intent = Intent(this, BillHistoryActivity::class.java)
            intent.putExtra(Utils.normalizePhone("customerPhone"), Utils.normalizePhone(customerPhone))
            startActivity(intent)
        }
    }
}