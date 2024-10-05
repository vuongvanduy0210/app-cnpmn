package com.kma_kit.smarthome.ui.activity

import RootController
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.kma_kit.smarthome.R
import com.kma_kit.smarthome.ui.fragment.HomeFragment
import com.kma_kit.smarthome.ui.fragment.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kma_kit.smarthome.data.model.response.UserResponse
import com.kma_kit.smarthome.ui.fragment.UsersFragment
import kotlinx.coroutines.launch

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView //
    private lateinit var userResponse: UserResponse
    private val rootController: RootController by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        bottomNavigationView = findViewById(R.id.bottomNavigationView) // Initialize from layout
        onInitData()

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_settings -> {
                    replaceFragment(SettingsFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_users -> {
                    replaceFragment(UsersFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                // Add more cases for other bottom navigation items here
            }
            false
        }

        rootController.error.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                // Hiển thị thông báo lỗi
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        // Set default fragment when activity is first created
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun onInitData() {
        lifecycleScope.launch {
            rootController.fetchUserInfo()
            rootController.userInfo.observe(this@HomeScreenActivity) {
                userResponse = it
            }
        }
    }
}
