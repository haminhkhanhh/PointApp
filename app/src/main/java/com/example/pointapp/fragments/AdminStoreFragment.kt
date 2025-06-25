package com.example.pointapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pointapp.R
import android.app.AlertDialog
import android.content.Intent
import android.view.*
import android.widget.*
import com.example.pointapp.activities.AdminEditStoreActivity
import com.example.pointapp.model.Store
import com.google.firebase.firestore.FirebaseFirestore

class AdminStoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_admin_store, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadStoreList(view)
    }

    private fun loadStoreList(root: View) {
        val db = FirebaseFirestore.getInstance()
        val container = root.findViewById<LinearLayout>(R.id.containerStoreRows)
        container.removeAllViews()

        db.collection("stores")
            .get()
            .addOnSuccessListener { snapshot ->
                val storeList = snapshot.toObjects(Store::class.java)
                for (store in storeList) {
                    // Tạo dòng cửa hàng
                    val row = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.HORIZONTAL
                        setPadding(0, 12, 0, 12)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }
                    val tvName = TextView(requireContext()).apply {
                        text = store.storeName
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                    }
                    val tvAddress = TextView(requireContext()).apply {
                        text = store.address
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                    }
                    val tvPhone = TextView(requireContext()).apply {
                        text = store.storePhone
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f)
                    }
                    val actions = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.3f)
                    }
                    val btnEdit = Button(requireContext()).apply {
                        text = "SỬA"
                        textSize = 12f
                        setOnClickListener {
                            val intent = Intent(requireContext(), AdminEditStoreActivity::class.java)
                            intent.putExtra("storeId", store.storeId)
                            startActivity(intent)
                        }
                    }
                    val btnDelete = Button(requireContext()).apply {
                        text = "XOÁ"
                        setBackgroundColor(resources.getColor(android.R.color.holo_red_light, null))
                        setTextColor(resources.getColor(android.R.color.white, null))
                        textSize = 12f
                        setOnClickListener {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Xác nhận xoá")
                                .setMessage("Xoá cửa hàng ${store.storeName}?")
                                .setPositiveButton("Xoá") { _, _ -> deleteStore(store.storeId) }
                                .setNegativeButton("Huỷ", null)
                                .show()
                        }
                    }
                    actions.addView(btnEdit)
                    val space = Space(requireContext())
                    space.layoutParams = LinearLayout.LayoutParams(12, 1)
                    actions.addView(space)
                    actions.addView(btnDelete)

                    row.addView(tvName)
                    row.addView(tvAddress)
                    row.addView(tvPhone)
                    row.addView(actions)

                    container.addView(row)
                }
            }
    }

    private fun deleteStore(storeId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("stores").document(storeId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Đã xoá!", Toast.LENGTH_SHORT).show()
                view?.let { loadStoreList(it) }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Lỗi xoá cửa hàng!", Toast.LENGTH_SHORT).show()
            }
    }
}