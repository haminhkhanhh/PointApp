package com.example.pointapp.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.pointapp.Utils
import com.google.firebase.firestore.FirebaseFirestore
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class AdminScanQRCodeActivity : AppCompatActivity() {

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val memberCode = result.contents
            fetchMemberInfoAndShowDialog(memberCode)
        } else {
            Toast.makeText(this, "Chưa quét được mã!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startScan()
    }

    private fun startScan() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Quét mã QR thành viên")
        options.setBeepEnabled(true)
        options.setOrientationLocked(false)
        barcodeLauncher.launch(options)
    }

    private fun fetchMemberInfoAndShowDialog(input: String) {
        val phone = Utils.normalizePhone(input)
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("phone", phone)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val userDoc = snapshot.documents[0]
                    val name = userDoc.getString("name") ?: "(Không có tên)"
                    val point = userDoc.getLong("point") ?: 0
                    val email = userDoc.getString("email") ?: ""
                    showMemberDialog(name, phone, point, email)
                } else {
                    showMemberDialog("Không tìm thấy", phone, 0, "")
                }
            }
            .addOnFailureListener {
                showMemberDialog("Lỗi kết nối", phone, 0, "")
            }
    }

    private fun showMemberDialog(name: String, phone: String, point: Long, email: String) {
        val info = """
            Họ tên: $name
            SĐT: $phone
            Điểm: $point
            Email: $email
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Thông tin thành viên")
            .setMessage(info)
            .setPositiveButton("OK") { _, _ -> finish() }
            .setOnDismissListener { finish() }
            .show()
    }
}