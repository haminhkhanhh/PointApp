package com.example.pointapp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pointapp.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.Utils
import com.example.pointapp.adapters.BillAdapter
import com.example.pointapp.adapters.BillHistoryAdapter
import com.example.pointapp.model.Bill
import com.example.pointapp.model.BillHistory
import com.google.firebase.firestore.FirebaseFirestore


class BillHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BillAdapter
    private lateinit var edtPhone: EditText
    private lateinit var btnLoad: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_history)

        recyclerView = findViewById(R.id.recyclerBills)
        edtPhone = findViewById(R.id.edtPhone)
        btnLoad = findViewById(R.id.btnLoad)

        adapter = BillAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnLoad.setOnClickListener {
            val phone = edtPhone.text.toString()
            if (phone.isNotBlank()) {
                loadBills(phone)
            } else {
                Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadBills(phone: String) {
        val db = FirebaseFirestore.getInstance()
        val normalizedPhone = normalizePhone(phone)

        db.collection("bills")
            .whereEqualTo("customerPhone", normalizedPhone)
            .get()
            .addOnSuccessListener { result ->
                val bills = result.documents.map { doc ->
                    Bill(
                        billId = doc.id,
                        customerPhone = doc.getString("customerPhone") ?: "",
                        discount = doc.getDouble("discount") ?: 0.0,
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        total = doc.getDouble("total") ?: 0.0,
                        voucherCode = doc.getString("voucherCode") ?: ""
                    )
                }
                adapter.updateBills(bills)
                if (bills.isEmpty()) {
                    Toast.makeText(this, "Không có hóa đơn nào", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi lấy dữ liệu!", Toast.LENGTH_SHORT).show()
            }
    }

    // Hàm normalizePhone như bạn đã cung cấp
    fun normalizePhone(phone: String): String {
        val raw = phone.trim().replace(" ", "")
        return when {
            raw.startsWith("+84") -> raw
            raw.startsWith("84") -> "+$raw"
            raw.startsWith("0") && raw.length > 1 -> "+84${raw.substring(1)}"
            else -> raw
        }
    }
}