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
import com.example.pointapp.model.Point
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
    private lateinit var imgPointer: ImageView

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
        imgPointer = view.findViewById(R.id.imgPointer)

        // Lấy dữ liệu user từ Firestore (ví dụ: họ tên, điểm)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            // 1. Lấy thông tin tên từ users/{uid}
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { docUser ->
                    val firstName = docUser.getString("firstName") ?: ""
                    val lastName = docUser.getString("lastName") ?: ""
                    tvCustomerName.text = "$firstName $lastName"

                    // 2. Lấy point từ collection 'point' (root)
                    db.collection("point").document(uid)
                        .get()
                        .addOnSuccessListener { docPoint ->
                            val pointObj = docPoint.toObject(Point::class.java)
                            val barWidth = progressBar.width
                            val maxPoint = 500.0
                            if (pointObj != null) {
                                tvPoints.text = pointObj.point.toInt().toString()
                                progressBar.progress = pointObj.point.toInt()
                                progressBar.post {
                                    val barWidth = progressBar.width.toDouble()
                                    val percent = (pointObj.point / maxPoint).coerceIn(0.0, 1.0) + 0.05
                                    val markerOffset = percent * barWidth - (imgPointer.width / 2)
                                    imgPointer.translationX = markerOffset.toFloat()
                                }
                            }

                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Không lấy được điểm: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
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