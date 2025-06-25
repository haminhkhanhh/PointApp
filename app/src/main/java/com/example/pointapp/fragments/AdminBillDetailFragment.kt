package com.example.pointapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.model.OrderBillItem
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AdminBillDetailFragment : Fragment() {

    companion object {
        fun newInstance(billId: String): AdminBillDetailFragment {
            val fragment = AdminBillDetailFragment()
            val args = Bundle()
            args.putString("billId", billId)
            fragment.arguments = args
            return fragment
        }
    }

    private var billId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billId = arguments?.getString("billId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_bill_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        billId?.let { id ->
            loadBillFromFirestore(id, view)
        }
    }

    private fun loadBillFromFirestore(billId: String, rootView: View) {
        val db = FirebaseFirestore.getInstance()
        db.collection("bills")
            .whereEqualTo("billId", billId)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val doc = snapshot.documents[0]
                    val id = doc.getString("billId") ?: ""
                    val timestamp = doc.getLong("timestamp") ?: 0L
                    val total = doc.getDouble("total") ?: 0.0
                    val phone = doc.getString("customerPhone") ?: ""
                    val voucher = doc.getString("voucherCode") ?: ""
                    val discount = doc.getDouble("discount") ?: 0.0
                    val final = (total).coerceAtLeast(0.0)

                    rootView.findViewById<TextView>(R.id.tvBillId)?.text = "ID: $id"
                    rootView.findViewById<TextView>(R.id.tvBillTime)?.text =
                        "Thời gian: " + SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
                    rootView.findViewById<TextView>(R.id.tvBillPhone)?.text = "SĐT: $phone"
                    rootView.findViewById<TextView>(R.id.tvBillVoucher)?.text = "Voucher: $voucher"
                    rootView.findViewById<TextView>(R.id.tvBillTotal)?.text = "Tổng tiền: ${total.toInt()}đ"
                    rootView.findViewById<TextView>(R.id.tvBillFinal)?.text = "Thành tiền: ${final.toInt()}đ"

                    // Lấy danh sách món như cũ...

                    val orderList = (doc.get("orderList") as? List<Map<String, Any>>)?.map { map ->
                        OrderBillItem(
                            productName = map["productName"] as? String ?: "",
                            sugar = map["sugar"] as? String ?: "",
                            ice = map["ice"] as? String ?: "",
                            toppingNames = (map["toppingNames"] as? List<*>)?.map { it.toString() } ?: emptyList(),
                            quantity = (map["quantity"] as? Long)?.toInt() ?: 1,
                            unitPrice = (map["unitPrice"] as? Long)?.toInt()
                                ?: (map["unitPrice"] as? Double)?.toInt() ?: 0
                        )
                    } ?: emptyList()

                    val rvOrderItems = rootView.findViewById<RecyclerView>(R.id.rvOrderItems)
                    rvOrderItems.layoutManager = LinearLayoutManager(requireContext())
                    rvOrderItems.adapter = OrderBillItemAdapter(orderList)

                    // Truy vấn sang users để lấy tên khách hàng
                    if (phone.isNotEmpty()) {
                        db.collection("users")
                            .whereEqualTo("phone", phone)
                            .limit(1)
                            .get()
                            .addOnSuccessListener { userSnap ->
                                if (!userSnap.isEmpty) {
                                    val userDoc = userSnap.documents[0]
                                    val customerName = userDoc.getString("lastName") ?: "(Không có tên)"
                                    rootView.findViewById<TextView>(R.id.tvBillCustomerName)?.text = "Tên: $customerName"
                                } else {
                                    rootView.findViewById<TextView>(R.id.tvBillCustomerName)?.text = "Tên: (Không tìm thấy)"
                                }
                            }
                    } else {
                        rootView.findViewById<TextView>(R.id.tvBillCustomerName)?.text = "Tên: (Không có số ĐT)"
                    }
                }
            }
    }
}