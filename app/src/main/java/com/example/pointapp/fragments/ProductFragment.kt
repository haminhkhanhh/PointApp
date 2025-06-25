package com.example.pointapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.adapters.SectionedProductAdapter
import com.example.pointapp.model.Category
import com.example.pointapp.model.Product
import com.google.firebase.firestore.FirebaseFirestore

class ProductFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadCategoriesAndProducts()
    }

    private fun loadCategoriesAndProducts() {
        val db = FirebaseFirestore.getInstance()
        val categories = mutableListOf<Category>()
        val rvProductSections = view?.findViewById<RecyclerView>(R.id.rvProductSections)

        db.collection("categories")
            .orderBy("order")
            .get()
            .addOnSuccessListener { catResult ->
                for (doc in catResult) {
                    categories.add(
                        Category(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            order = (doc.getLong("order") ?: 0).toInt()
                        )
                    )
                }
                // Lấy sản phẩm
                db.collection("products")
                    .get()
                    .addOnSuccessListener { prodResult ->
                        val products = mutableListOf<Product>()
                        for (doc in prodResult) {
                            products.add(
                                Product(
                                    id = doc.id,
                                    name = doc.getString("name") ?: "",
                                    desc = doc.getString("desc") ?: "",
                                    price = doc.getDouble("price") ?: 0.0,
                                    imageUrl = doc.getString("imageUrl") ?: "",
                                    categoryId = doc.getString("categoryId") ?: ""
                                )
                            )
                        }
                        // Hiển thị lên RecyclerView
                        rvProductSections?.layoutManager = LinearLayoutManager(requireContext())
                        rvProductSections?.adapter = SectionedProductAdapter(categories, products) { prod ->
                            // Xử lý khi click sản phẩm
                            // Toast.makeText(requireContext(), prod.name, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
    }
}