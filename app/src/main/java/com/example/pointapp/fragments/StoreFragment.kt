package com.example.pointapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pointapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StoreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StoreFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_store, container, false)

        // Gắn MapFragment vào container
        mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.mapContainer, mapFragment)
            .commit()

        mapFragment.getMapAsync(this) // gọi callback khi bản đồ sẵn sàng

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val center = LatLng(10.776889, 106.700806)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 12.5f))

        val cheeseCoffees = listOf(
            Triple("Cheese Coffee Nguyễn Thị Minh Khai", 10.770163, 106.684210),
            Triple("Cheese Coffee Võ Văn Tần", 10.775194, 106.685770),
            Triple("Cheese Coffee Pasteur", 10.776666, 106.699540),
            Triple("Cheese Coffee Hoàng Diệu", 10.759822, 106.697631),
            Triple("Cheese Coffee Vinhomes Central Park", 10.794217, 106.721300),
            Triple("Cheese Coffee Quang Trung", 10.834095, 106.660868),
            Triple("Cheese Coffee Âu Cơ", 10.785245, 106.637586)
        )

        cheeseCoffees.forEach { (name, lat, lng) ->
            val position = LatLng(lat, lng)
            googleMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(name)
            )
        }
    }
}