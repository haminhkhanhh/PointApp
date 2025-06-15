package com.example.pointapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.example.pointapp.R
import com.example.pointapp.activities.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    private lateinit var tvGreeting: TextView
    private lateinit var tvCustomerName: TextView
    private lateinit var tvPoints: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var seekFeedback: SeekBar
    private lateinit var imgBanner: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        tvGreeting = view.findViewById(R.id.tvGreeting)
        tvCustomerName = view.findViewById(R.id.tvCustomerName)
        tvPoints = view.findViewById(R.id.tvPoints)
        progressBar = view.findViewById(R.id.progressBar)
        seekFeedback = view.findViewById(R.id.seekFeedback)
        imgBanner = view.findViewById(R.id.imgBanner)

        // Lấy dữ liệu user từ Firestore (ví dụ: họ tên, điểm)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener { doc ->
                    val name = doc.getString("firstName") ?: ""
                    val lastName = doc.getString("lastName") ?: ""
                    tvCustomerName.text = "$name $lastName"
                    val points = doc.getLong("points") ?: 0
                    tvPoints.text = points.toString()
                    progressBar.progress = points.toInt()
                }
        }

        // Bắt sự kiện các nút (ví dụ)
        view.findViewById<LinearLayout>(R.id.imgProfile).setOnClickListener {
            Toast.makeText(context, "Profile clicked!", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
        view.findViewById<LinearLayout>(R.id.imgInbox).setOnClickListener {
            Toast.makeText(context, "Inbox clicked!", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<ImageView>(R.id.imgSettings).setOnClickListener {
            Toast.makeText(context, "Settings clicked!", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<Button>(R.id.btnDetails).setOnClickListener {
            Toast.makeText(context, "Details clicked!", Toast.LENGTH_SHORT).show()
        }
        view.findViewById<Button>(R.id.btnBenefits).setOnClickListener {
            Toast.makeText(context, "Benefits clicked!", Toast.LENGTH_SHORT).show()
        }
        // Feedback
        seekFeedback.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                // Tuỳ logic: highlight mặt cảm xúc, gửi feedback v.v.
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // Bạn có thể load ảnh banner bằng Glide/Picasso nếu muốn
        // Glide.with(this).load(url).into(imgBanner)

        return view
    }
}