package com.example.pointapp.repository

import com.example.pointapp.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {
    private val db = FirebaseFirestore.getInstance()
    private val userCollection = db.collection("users")

    // Tạo user mới với id tự sinh
    fun createUser(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        birthDay: Int,
        birthMonth: Int,
        birthYear: Int,
        city: String,
        gender: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val newDocRef = userCollection.document()
        val autoId = newDocRef.id

        val user = User(
            id = autoId,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            birthDay = birthDay,
            birthMonth = birthMonth,
            birthYear = birthYear,
            city = city,
            gender = gender
        )

        newDocRef.set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Lấy user theo id
    fun getUserById(
        id: String,
        onSuccess: (User?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        userCollection.document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(User::class.java)
                    onSuccess(user)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Cập nhật thông tin user
    fun updateUser(
        user: User,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        userCollection.document(user.id).set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    // Xóa user
    fun deleteUser(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        userCollection.document(id).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }
}
