package com.example.pointapp.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.pointapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
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
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private lateinit var mapFragment: SupportMapFragment
    private var googleMap: GoogleMap? = null
    private lateinit var nearbyList: LinearLayout

    // Danh sách quán thực tế Cheese Coffee
    private val cheeseCoffees = listOf(
        Triple("Cửa hàng Phan Văn Trị", 10.813974, 106.695439),
        Triple("Cửa hàng Đinh Tiên Hoàng", 10.786145, 106.702117),
        Triple("Cửa hàng Điện Biên Phủ", 10.801985, 106.71464),
        Triple("Cửa hàng Gigamall", 10.828106, 106.721305),
        Triple("Cửa hàng Xô Viết Nghệ Tĩnh", 10.795348, 106.709131),
        Triple("Cửa hàng Pasteur", 10.77014, 106.702803),
        Triple("Cửa hàng Công Trường Lam Sơn", 10.777422, 106.704015),
        Triple("Cửa hàng Sư Vạn Hạnh", 10.77416, 106.668734),
        Triple("Cửa hàng Hồng Bàng", 10.754886, 106.660253),
        Triple("Cửa hàng Lê Thị Riêng", 10.771645, 106.691548),
        Triple("Cửa hàng Hoa Lan", 10.79683, 106.690379),
        Triple("Cửa hàng Bùi Viện", 10.768344, 106.694675),
        Triple("Cửa hàng Estella", 10.802366, 106.747883),
        Triple("Cửa hàng 157 Lê Thánh Tôn", 10.774195, 106.69888),
        Triple("Cửa hàng Nguyễn Hồng Đào", 10.794052, 106.642062),
        Triple("Cửa hàng 13 Lê Thánh Tôn", 10.781653, 106.705576),
        Triple("Cửa hàng Nguyễn Du", 10.780385, 106.700841),
        Triple("Cửa hàng Thành Thái", 10.775752, 106.664192),
        Triple("Cửa hàng Cộng Hoà", 10.801223, 106.65647),
        Triple("Cửa hàng Lê Đại Hành - Hà Nội", 21.011837, 105.848276),
        Triple("Cửa hàng Trần Phú - Đà Nẵng", 16.065685, 108.22336)
    )

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getUserLocationAndShowOnMap()
            } else {
                showShops(null)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_store, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        nearbyList = view.findViewById(R.id.nearbyList)

        // Khởi tạo map
        mapFragment = childFragmentManager.findFragmentById(R.id.mapContainer) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction()
                    .replace(R.id.mapContainer, it)
                    .commit()
            }
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // Xin quyền location
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getUserLocationAndShowOnMap()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getUserLocationAndShowOnMap() {
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    currentLocation = location
                    showShops(location)
                }
        } catch (e: SecurityException) {
            showShops(null)
        }
    }

    private fun showShops(userLocation: Location?) {
        googleMap?.clear()
        nearbyList.removeAllViews()

        val markerList = mutableListOf<Marker>()

        // Sắp xếp theo khoảng cách
        val sortedShops = if (userLocation != null) {
            cheeseCoffees.map { (name, lat, lng) ->
                val dist = calculateDistance(userLocation, lat, lng)
                Triple(name, lat, lng) to dist
            }.sortedBy { it.second }
        } else {
            cheeseCoffees.map { Triple(it.first, it.second, it.third) to null }
        }

        // Thêm marker và danh sách item
        sortedShops.forEachIndexed { index, (shop, distance) ->
            val (name, lat, lng) = shop
            var snippet: String? = null
            var displayDistance = ""
            if (distance != null) {
                snippet = String.format("Cách bạn: %.1f km", distance / 1000)
                displayDistance = "%.1f km từ vị trí của bạn".format(distance / 1000)
            }

            // Thêm marker trên map
            val marker = googleMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(lat, lng))
                    .title(name)
                    .snippet(snippet)
            )
            markerList.add(marker!!)

            // Inflate layout item
            val itemView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_nearby_store, nearbyList, false)
            itemView.findViewById<TextView>(R.id.tvAddress).text = name
            itemView.findViewById<TextView>(R.id.tvDistance).text = displayDistance

            // Bấm vào list thì zoom tới marker
            itemView.setOnClickListener {
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 15f))
                marker.showInfoWindow()
            }
            nearbyList.addView(itemView)
        }

        // Marker vị trí user (nếu có)
        userLocation?.let {
            val userLatLng = LatLng(it.latitude, it.longitude)
            googleMap?.addMarker(
                MarkerOptions()
                    .position(userLatLng)
                    .title("Bạn đang ở đây")
            )
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 13f))
        } ?: run {
            // Không có vị trí thì lấy trung tâm TP.HCM
            val hcmCenter = LatLng(10.776889, 106.700806)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmCenter, 12.5f))
        }
    }

    private fun calculateDistance(current: Location, targetLat: Double, targetLng: Double): Float {
        val targetLocation = Location("").apply {
            latitude = targetLat
            longitude = targetLng
        }
        return current.distanceTo(targetLocation)
    }
}