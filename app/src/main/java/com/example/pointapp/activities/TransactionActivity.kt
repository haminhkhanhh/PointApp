package com.example.pointapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pointapp.R
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.adapters.BillHistoryAdapter
import com.example.pointapp.model.BillHistory
import com.google.firebase.firestore.FirebaseFirestore

class TransactionActivity : AppCompatActivity() {
    private lateinit var adapter: BillHistoryAdapter
    private val billList = mutableListOf<BillHistory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

////        val rv = findViewById<RecyclerView>(R.id.rvBillHistory)
//        rv.layoutManager = LinearLayoutManager(this)
//        adapter = BillHistoryAdapter(billList)
//        rv.adapter = adapter

        // Lấy customerPhone truyền qua intent
        val customerPhone = intent.getStringExtra("customerPhone") ?: ""
        if (customerPhone.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy số điện thoại khách hàng", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseFirestore.getInstance().collection("bills")
            .whereEqualTo("customerPhone", customerPhone)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snap ->
                billList.clear()
                for (doc in snap.documents) {
                    val bill = doc.toObject(BillHistory::class.java)
                    if (bill != null) billList.add(bill)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}