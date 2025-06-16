package com.example.pointapp.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.pointapp.R
import com.example.pointapp.databinding.ActivityAdminMainBinding
import com.example.pointapp.fragments.AdminOrderFragment
import com.example.pointapp.fragments.AdminRewardFragment
import com.example.pointapp.fragments.AdminStoreFragment
import com.example.pointapp.fragments.AdminUserFragment

class AdminMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adminOrderFragment = AdminOrderFragment()
        val adminUserFragment = AdminUserFragment()
        val adminStoreFragment = AdminStoreFragment()
        val adminRewardFragment = AdminRewardFragment()

        makeCurrentFragment(adminOrderFragment)

        binding.bottomNavBarAdmin.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.createOrder -> makeCurrentFragment(adminOrderFragment)
                R.id.member -> makeCurrentFragment(adminUserFragment)
                R.id.store -> makeCurrentFragment(adminStoreFragment)
                R.id.reward -> makeCurrentFragment(adminRewardFragment)
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, fragment)
            commit()
        }
}