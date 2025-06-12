package com.example.pointapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.pointapp.R
import com.example.pointapp.databinding.ActivityMainBinding
import com.example.pointapp.fragments.HomeFragment
import com.example.pointapp.fragments.MembershipFragment
import com.example.pointapp.fragments.StoreFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val homeFragment = HomeFragment()
        val membershipFragment = MembershipFragment()
        val storeFragment = StoreFragment()

        makeCurrentFragment(homeFragment)

        binding.bottomNavBar.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home -> makeCurrentFragment(homeFragment)
                R.id.membership -> makeCurrentFragment(membershipFragment)
                R.id.store -> makeCurrentFragment(storeFragment)
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