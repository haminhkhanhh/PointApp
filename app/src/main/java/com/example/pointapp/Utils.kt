package com.example.pointapp // hoặc com.example.pointapp.util nếu tạo package con

object Utils {
    /**
     * Chuẩn hóa số điện thoại về dạng +84xxx...
     * Hỗ trợ đầu vào: "098...", "84...", "+84..."
     */
    fun normalizePhone(phone: String): String {
        val raw = phone.trim().replace(" ", "")
        return when {
            raw.startsWith("+84") -> raw
            raw.startsWith("84") -> "+$raw"
            raw.startsWith("0") && raw.length > 1 -> "+84${raw.substring(1)}"
            else -> raw
        }
    }
}
