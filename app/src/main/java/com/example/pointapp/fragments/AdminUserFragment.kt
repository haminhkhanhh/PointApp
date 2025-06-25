package com.example.pointapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pointapp.R
import com.google.firebase.firestore.FirebaseFirestore
import android.app.AlertDialog
import android.content.Intent
import android.view.*
import android.widget.*
import com.example.pointapp.activities.AdminEditUserActivity

class AdminUserFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_admin_user, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserList(view)
    }

    private fun loadUserList(root: View) {
        val db = FirebaseFirestore.getInstance()
        val container = root.findViewById<LinearLayout>(R.id.containerUserRows)
        container.removeAllViews()

        db.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    val userId = doc.id
                    val firstName = doc.getString("firstName") ?: ""
                    val lastName = doc.getString("lastName") ?: ""
                    val fullName = "$firstName $lastName".trim()
                    val phone = doc.getString("phone") ?: ""
                    val email = doc.getString("email") ?: ""

                    // Tạo layout dòng user
                    val row = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.HORIZONTAL
                        setPadding(0, 12, 0, 12)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }
                    // Họ tên
                    val tvName = TextView(requireContext()).apply {
                        text = fullName
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                    }
                    // SĐT
                    val tvPhone = TextView(requireContext()).apply {
                        text = phone
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                    }
                    // Email
                    val tvEmail = TextView(requireContext()).apply {
                        text = email
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f)
                    }
                    // Hành động (nút Edit, Delete)
                    val actions = LinearLayout(requireContext()).apply {
                        orientation = LinearLayout.VERTICAL
                        gravity = Gravity.CENTER
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.2f)
                    }

                    val btnEdit = Button(requireContext()).apply {
                        text = "SỬA"
                        setOnClickListener {
                            // Sang màn sửa user, truyền userId
                            val intent = Intent(requireContext(), AdminEditUserActivity::class.java)
                            intent.putExtra("userId", userId)
                            startActivity(intent)
                        }
                        textSize = 12f
                    }

                    val btnDelete = Button(requireContext()).apply {
                        text = "XOÁ"
                        setBackgroundColor(resources.getColor(android.R.color.holo_red_light, null))
                        setTextColor(resources.getColor(android.R.color.white, null))
                        textSize = 12f
                        setOnClickListener {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Xác nhận xoá")
                                .setMessage("Xoá người dùng $fullName?")
                                .setPositiveButton("Xoá") { _, _ -> deleteUser(userId) }
                                .setNegativeButton("Huỷ", null)
                                .show()
                        }
                    }

                    // Thêm nút Edit và Delete vào actions
                    actions.addView(btnEdit)
                    val space = Space(requireContext())
                    space.layoutParams = LinearLayout.LayoutParams(12, 1)
                    actions.addView(space)
                    actions.addView(btnDelete)

                    row.addView(tvName)
                    row.addView(tvPhone)
                    row.addView(tvEmail)
                    row.addView(actions)

                    container.addView(row)
                }
            }
    }

    private fun deleteUser(userId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Đã xoá!", Toast.LENGTH_SHORT).show()
                // Reload lại danh sách
                view?.let { loadUserList(it) }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Lỗi xoá user!", Toast.LENGTH_SHORT).show()
            }
    }
}