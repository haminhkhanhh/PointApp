package com.example.pointapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pointapp.R
import com.example.pointapp.Utils
import com.example.pointapp.model.User
import com.example.pointapp.repository.UserRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeParseException
import java.util.concurrent.TimeUnit

class CompleteProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private val userRepo = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)

        auth = FirebaseAuth.getInstance()

        val edtFirstName = findViewById<EditText>(R.id.edtFirstName)
        val edtLastName = findViewById<EditText>(R.id.edtLastName)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPhone = findViewById<EditText>(R.id.edtPhone)
        val edtDay = findViewById<EditText>(R.id.edtDay)
        val edtMonth = findViewById<EditText>(R.id.edtMonth)
        val edtYear = findViewById<EditText>(R.id.edtYear)
        val edtCity = findViewById<EditText>(R.id.edtCity)
        val spGender = findViewById<Spinner>(R.id.spGender)
        val btnSendOtp = findViewById<Button>(R.id.btnSendOtp)
        val edtOtp = findViewById<EditText>(R.id.edtOtp)
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)

        // Hiển thị email không cho sửa
        edtEmail.setText(auth.currentUser?.email ?: "")
        edtEmail.isEnabled = false

        // Thiết lập Spinner giới tính
        val genderOptions = arrayOf("Nam", "Nữ", "Khác")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGender.adapter = adapter

        // Gửi OTP
        btnSendOtp.setOnClickListener {
            val phone = Utils.normalizePhone(edtPhone.text.toString())
            if (phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        edtOtp.setText(credential.smsCode ?: "")
                    }
                    override fun onVerificationFailed(e: FirebaseException) {
                        Toast.makeText(this@CompleteProfileActivity, "OTP lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                    override fun onCodeSent(vid: String, token: PhoneAuthProvider.ForceResendingToken) {
                        verificationId = vid
                        Toast.makeText(this@CompleteProfileActivity, "Đã gửi OTP", Toast.LENGTH_SHORT).show()
                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }

        // Cập nhật profile
        btnUpdate.setOnClickListener {
            val firstName = edtFirstName.text.toString().trim()
            val lastName = edtLastName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val phone = Utils.normalizePhone(edtPhone.text.toString())
            val city = edtCity.text.toString().trim()
            val gender = spGender.selectedItem.toString()
            val day = edtDay.text.toString().toIntOrNull() ?: 0
            val month = edtMonth.text.toString().toIntOrNull() ?: 0
            val year = edtYear.text.toString().toIntOrNull() ?: 0
            val otp = edtOtp.text.toString().trim()
            val verId = verificationId

            val errorMsg = validateProfile(
                firstName, lastName, email, phone, day, month, year, city, gender, otp, verId
            )
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val credential = PhoneAuthProvider.getCredential(verId!!, otp)
            // Liên kết hoặc cập nhật số điện thoại với Firebase Auth
            auth.currentUser?.linkWithCredential(credential)
                ?.addOnSuccessListener {
                    saveUser(firstName, lastName, email, phone, day, month, year, city, gender)
                }
                ?.addOnFailureListener { e ->
                    if (e is FirebaseAuthUserCollisionException ||
                        e.message?.contains("already been linked") == true
                    ) {
                        auth.currentUser?.updatePhoneNumber(credential)
                            ?.addOnSuccessListener {
                                saveUser(firstName, lastName, email, phone, day, month, year, city, gender)
                            }
                            ?.addOnFailureListener { err ->
                                Toast.makeText(this, "Không thể cập nhật số điện thoại: ${err.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "OTP hoặc liên kết số điện thoại lỗi: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun saveUser(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        day: Int,
        month: Int,
        year: Int,
        city: String,
        gender: String
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val user = User(
            id = uid,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            birthDay = day,
            birthMonth = month,
            birthYear = year,
            city = city,
            gender = gender
        )
        userRepo.updateUser(
            user = user,
            onSuccess = {
                Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            },
            onFailure = { e ->
                Toast.makeText(this, "Lỗi lưu thông tin: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun isValidDate(day: Int, month: Int, year: Int): Boolean {
        return try {
            LocalDate.of(year, month, day)
            true
        } catch (e: DateTimeParseException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    private fun isOver16(day: Int, month: Int, year: Int): Boolean {
        return try {
            val birthDate = LocalDate.of(year, month, day)
            val now = LocalDate.now()
            val age = Period.between(birthDate, now).years
            age >= 17
        } catch (e: Exception) {
            false
        }
    }

    private fun validateProfile(
        firstName: String, lastName: String, email: String, phone: String,
        day: Int, month: Int, year: Int, city: String, gender: String,
        otp: String, verId: String?
    ): String? {
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()
            || day == 0 || month == 0 || year == 0 || city.isEmpty()
        ) return "Vui lòng nhập đầy đủ thông tin!"
        if (!isValidDate(day, month, year)) return "Ngày sinh không hợp lệ!"
        if (!isOver16(day, month, year)) return "Bạn phải đủ 17 tuổi trở lên để đăng ký!"
        if (verId.isNullOrEmpty() || otp.isEmpty()) return "Bạn cần xác thực OTP số điện thoại!"
        return null
    }
}
