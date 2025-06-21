package com.example.pointapp.activities

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pointapp.R
import com.example.pointapp.adapters.ProductAdapter
import com.example.pointapp.model.Product
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore

class AdminCreateOrderActivity : AppCompatActivity() {
    private lateinit var adapter: ProductAdapter
    private val categoryList = mutableListOf<Pair<String, String>>() // Pair<categoryId, categoryName>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_create_order)

        // Nếu bạn dùng ViewCompat để căn lề:
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Adapter setup
        adapter = ProductAdapter(listOf()) { product ->
            Toast.makeText(this, "Đã thêm: ${product.name}", Toast.LENGTH_SHORT).show()
        }
        val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvProductList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load category lên TabLayout
        loadCategories()

        // SearchView filter
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    private fun loadCategories() {
        FirebaseFirestore.getInstance().collection("categories")
            .orderBy("order")
            .get().addOnSuccessListener { snapshot ->
                val tabLayout = findViewById<TabLayout>(R.id.tabLayoutCategory)
                tabLayout.removeAllTabs()
                categoryList.clear()
                snapshot.forEach { doc ->
                    val categoryId = doc.id
                    val categoryName = doc.getString("name") ?: ""
                    categoryList.add(Pair(categoryId, categoryName))
                    tabLayout.addTab(tabLayout.newTab().setText(categoryName))
                }
                // Load sản phẩm cho tab đầu tiên (nếu có)
                if (categoryList.isNotEmpty()) {
                    loadProductsByCategory(categoryList[0].first)
                }
                tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        val idx = tab?.position ?: 0
                        loadProductsByCategory(categoryList[idx].first)
                    }
                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                })
            }
    }

    private fun loadProductsByCategory(categoryId: String) {
        FirebaseFirestore.getInstance().collection("products")
            .whereEqualTo("categoryId", categoryId)
            .get().addOnSuccessListener { snap ->
                val list = snap.documents.mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) }
                adapter.updateList(list)
            }
    }
}