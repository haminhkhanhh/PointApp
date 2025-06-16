package com.example.pointapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.pointapp.R
import com.example.pointapp.activities.AdminCreateOrderActivity
import com.example.pointapp.activities.AdminViewOrdersActivity
import com.example.pointapp.activities.AdminScanQRCodeActivity
import com.example.pointapp.activities.ProfileActivity

class AdminOrderFragment : Fragment() {

    private lateinit var tvAdminName: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin_order, container, false)

        // Set tên admin (có thể lấy từ Bundle/Argument hoặc SharedPreferences/Firebase)
        val tvAdminName = view.findViewById<TextView>(R.id.tvAdminName)
        tvAdminName.text = "Nguyễn Văn A" // Tùy biến nếu lấy động

        // Nút Lập hóa đơn
        view.findViewById<View>(R.id.btnCreateOrder).setOnClickListener {
            Toast.makeText(requireContext(), "Lập hóa đơn", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), AdminCreateOrderActivity::class.java)
            startActivity(intent)
        }

        // Nút Xem hóa đơn
        view.findViewById<View>(R.id.btnViewOrders).setOnClickListener {
            Toast.makeText(requireContext(), "Xem hóa đơn đã lập", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), AdminViewOrdersActivity::class.java)
            startActivity(intent)
        }

        // Nút Quét QR thành viên
        view.findViewById<View>(R.id.btnViewMemberByQR).setOnClickListener {
            Toast.makeText(requireContext(), "Quét QR thành viên", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), AdminScanQRCodeActivity::class.java)
            startActivity(intent)
        }

        // Hồ sơ
        view.findViewById<LinearLayout>(R.id.imgProfile).setOnClickListener {
            Toast.makeText(requireContext(), "Xem hồ sơ", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        // Hộp thư
        view.findViewById<LinearLayout>(R.id.imgInbox).setOnClickListener {
            Toast.makeText(requireContext(), "Xem hộp thư", Toast.LENGTH_SHORT).show()
            // TODO: Chuyển sang màn hình Hộp thư
        }

        // Cài đặt
        view.findViewById<ImageView>(R.id.imgSettings).setOnClickListener {
            Toast.makeText(requireContext(), "Cài đặt đã được click", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}