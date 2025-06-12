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

    fun isValidVietnamesePhoneNumber(input: String): Boolean {
        // Loại bỏ các ký tự không phải số
        val number = input.replace("[^0-9]".toRegex(), "")

        // Nếu nhập đầu +84 hoặc 84, chuyển về 0 để kiểm tra đầu số
        val normalized = when {
            number.startsWith("84") && number.length == 11 -> "0" + number.substring(2)
            number.startsWith("0") && number.length == 10 -> number
            else -> number
        }

        // Danh sách đầu số hợp lệ (cập nhật năm 2024, có thể thêm các đầu số mới nếu cần)
        val validPrefixes = listOf(
            "032", "033", "034", "035", "036", "037", "038", "039", // Viettel
            "070", "076", "077", "078", "079",                     // MobiFone
            "081", "082", "083", "084", "085",                     // VinaPhone
            "056", "058",                                          // Vietnamobile
            "059",                                                 // Gmobile
            "086", "096", "097", "098",                            // Viettel cũ
            "089", "090", "093",                                   // MobiFone cũ
            "091", "094", "088"                                    // VinaPhone cũ
        )

        // Kiểm tra độ dài & đầu số
        return normalized.length == 10 && validPrefixes.any { normalized.startsWith(it) }
    }
}
