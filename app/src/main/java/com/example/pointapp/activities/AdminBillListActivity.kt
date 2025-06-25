package com.example.pointapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.fragments.AdminBillDetailFragment
import com.example.pointapp.R
import com.example.pointapp.adapters.BillListAdapter
import com.example.pointapp.model.BillAdmin
import com.google.firebase.firestore.FirebaseFirestore

class AdminBillListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val billList = mutableListOf<BillAdmin>()
    private lateinit var adapter: BillListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_bill_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Danh sách hóa đơn"

        recyclerView = findViewById(R.id.rvBills)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BillListAdapter(billList) { billId ->
            showBillDetailFragment(billId)
        }
        recyclerView.adapter = adapter

        loadBills()
    }

    private fun loadBills() {
        val db = FirebaseFirestore.getInstance()
        db.collection("bills")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                billList.clear()
                for (doc in snapshot.documents) {
                    val billId = doc.getString("billId") ?: ""
                    val timestamp = doc.getLong("timestamp") ?: 0L
                    val total = doc.getDouble("total") ?: 0.0
                    billList.add(BillAdmin(billId, timestamp, total))
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun showBillDetailFragment(billId: String) {
        recyclerView.visibility = View.GONE
        findViewById<FrameLayout>(R.id.fragmentContainer).visibility = View.VISIBLE

        val fragment = AdminBillDetailFragment.newInstance(billId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Xử lý hiển thị lại list khi back
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            findViewById<FrameLayout>(R.id.fragmentContainer).visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }
}
