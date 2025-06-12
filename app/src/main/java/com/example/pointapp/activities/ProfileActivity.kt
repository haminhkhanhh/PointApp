package com.example.pointapp.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.pointapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser!!.uid

        val emailView = findViewById<TextView>(R.id.txtProfileEmail)
        val nameEdit = findViewById<EditText>(R.id.edtProfileName)
        val phoneEdit = findViewById<EditText>(R.id.edtProfilePhone)

        db.collection("users").document(uid).get().addOnSuccessListener {
            emailView.text = it.getString("email")
            val profile = it.get("profile") as? Map<*, *>
            nameEdit.setText(profile?.get("name") as? String ?: "")
            phoneEdit.setText(profile?.get("phone") as? String ?: "")
        }

        findViewById<Button>(R.id.btnSaveProfile).setOnClickListener {
            val newProfile = hashMapOf(
                "name" to nameEdit.text.toString(),
                "phone" to phoneEdit.text.toString()
            )
            db.collection("users").document(uid).update("profile", newProfile)
                .addOnSuccessListener {
                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                }
        }
    }
}