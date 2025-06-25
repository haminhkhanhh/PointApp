package com.example.pointapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointapp.R
import com.example.pointapp.Utils
import com.example.pointapp.adapters.OrderItemAdapter
import com.example.pointapp.model.OrderItem
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CheckoutActivity : AppCompatActivity() {
    private val orderList = mutableListOf<OrderItem>()
    private lateinit var adapter: OrderItemAdapter
    private lateinit var edtCustomerCode: EditText
    private lateinit var edtVoucher: EditText
    private lateinit var btnScanQR: Button
    private lateinit var tvTotal: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        edtCustomerCode = findViewById(R.id.edtCustomerCode)
        edtVoucher = findViewById(R.id.edtVoucher)
        btnScanQR = findViewById(R.id.btnScanQR)
        tvTotal = findViewById(R.id.tvTotal)

        val received = intent.getParcelableArrayListExtra<OrderItem>("cartList") ?: arrayListOf()
        orderList.clear()
        orderList.addAll(received)

        val rvOrderItems = findViewById<RecyclerView>(R.id.rvOrderItems)
        adapter = OrderItemAdapter(orderList) { updateTotal() }
        rvOrderItems.layoutManager = LinearLayoutManager(this)
        rvOrderItems.adapter = adapter

        updateTotal()

        findViewById<Button>(R.id.btnCheckout).setOnClickListener {
            processCheckout()
        }

        btnScanQR.setOnClickListener { startQRScanner() }
    }

    private fun updateTotal() {
        val total = orderList.sumOf { (it.product.price + it.toppings.sumOf { t -> t.price }) * it.quantity }
        tvTotal.text = "Tổng cộng: ${total.toInt()}đ"
    }

    private fun processCheckout() {
        val customerPhoneInput = edtCustomerCode.text.toString().trim()
        val customerPhone = Utils.normalizePhone(customerPhoneInput)
        val voucherCode = edtVoucher.text.toString().trim()
        val total = orderList.sumOf { (it.product.price + it.toppings.sumOf { t -> t.price }) * it.quantity }

        // 1. Kiểm tra voucher (nếu có)
        if (voucherCode.isNotEmpty()) {
            db.collection("vouchers")
                .whereEqualTo("code", voucherCode)
                .whereEqualTo("phoneNumber", customerPhone)
                .limit(1)
                .get()
                .addOnSuccessListener { voucherSnap ->
                    if (voucherSnap.isEmpty) {
                        Toast.makeText(this, "Mã voucher không đúng hoặc không thuộc về khách này!", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    val voucherDoc = voucherSnap.documents[0]
                    val isUsed = voucherDoc.getBoolean("isUsed") ?: false
                    val expireTimestamp = voucherDoc.getTimestamp("expireDate")
                    val expireDate = expireTimestamp?.toDate()
                    val value = voucherDoc.getLong("value") ?: 0

                    val now = Date()
                    if (expireDate != null && now.after(expireDate)) {
                        Toast.makeText(this, "Voucher đã hết hạn!", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    if (isUsed) {
                        Toast.makeText(this, "Voucher này đã được sử dụng!", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    // Đánh dấu đã dùng voucher
                    db.collection("vouchers").document(voucherDoc.id)
                        .update("isUsed", true)

                    // Lưu lịch sử dùng voucher (tùy chọn)
                    val voucherHistory = hashMapOf(
                        "voucherCode" to voucherCode,
                        "customerPhone" to customerPhone,
                        "time" to System.currentTimeMillis()
                    )
                    db.collection("voucher_usages").add(voucherHistory)

                    // Hoàn tất thanh toán
                    doCheckout(customerPhone, total, value.toDouble(), voucherCode)
                }
        } else {
            // Không có voucher, thanh toán bình thường
            doCheckout(customerPhone, total, 0.0, null)
        }
    }

    private fun doCheckout(customerPhone: String, total: Double, discount: Double, voucherCode: String?) {
        val finalTotal = (total - discount).coerceAtLeast(0.0)
        val billId = db.collection("bills").document().id
        val bill = hashMapOf(
            "billId" to billId,
            "customerPhone" to customerPhone,
            "voucherCode" to (voucherCode ?: ""),
            "orderList" to orderList.map { item ->
                hashMapOf(
                    "productName" to item.product.name,
                    "sugar" to item.sugar,
                    "ice" to item.ice,
                    "toppingNames" to item.toppings.map { it.name },
                    "quantity" to item.quantity,
                    "unitPrice" to item.product.price + item.toppings.sumOf { t -> t.price }
                )
            },
            "total" to finalTotal,
            "discount" to discount,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("bills").document(billId).set(bill)
            .addOnSuccessListener {
                // Nếu có số điện thoại, mới cộng điểm
                if (customerPhone.isNotEmpty()) {
                    db.collection("users")
                        .whereEqualTo("phone", customerPhone)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { userSnap ->
                            if (!userSnap.isEmpty) {
                                val userDoc = userSnap.documents[0]
                                val currentPoint = userDoc.getLong("point") ?: 0
                                val pointToAdd = (finalTotal / 10000).toInt()
                                db.collection("users").document(userDoc.id)
                                    .update("point", currentPoint + pointToAdd)

                                // Ghi lịch sử đổi điểm
                                val pointHistory = hashMapOf(
                                    "customerPhone" to customerPhone,
                                    "type" to "add",
                                    "pointChanged" to pointToAdd,
                                    "billId" to billId,
                                    "timestamp" to System.currentTimeMillis()
                                )
                                db.collection("point_histories").add(pointHistory)

                                Toast.makeText(this, "Thanh toán thành công!\nKhách hàng được cộng $pointToAdd điểm!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this, "Thanh toán thành công!\nKhông cộng điểm vì không tìm thấy khách hàng.", Toast.LENGTH_LONG).show()
                            }
                            val intent = Intent(this, AdminMainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                } else {
                    Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, AdminMainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
    }

    private val barcodeLauncher = registerForActivityResult(com.journeyapps.barcodescanner.ScanContract()) { result ->
        if (result.contents != null) {
            edtCustomerCode.setText(result.contents)
        }
    }
    private fun startQRScanner() {
        val options = com.journeyapps.barcodescanner.ScanOptions()
        options.setDesiredBarcodeFormats(com.journeyapps.barcodescanner.ScanOptions.QR_CODE)
        options.setPrompt("Quét mã QR khách hàng")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        barcodeLauncher.launch(options)
    }
}