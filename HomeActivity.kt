package com.example.plottwisterapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
//import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Views
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val isGuest = sharedPreferences.getBoolean("isGuest", false)
        if (isGuest) {
            bottomNavigation.menu.findItem(R.id.nav_write).isEnabled = false
            bottomNavigation.menu.findItem(R.id.nav_saved_stories).isEnabled = false
        }

        val openFragment = intent.getStringExtra("OPEN_FRAGMENT")
        if (openFragment == "SavedStoriesFragment") {
            loadFragment(SavedStoriesFragment())
            bottomNavigation.selectedItemId = R.id.nav_saved_stories
        } else {
            loadFragment(HomeFragment()) // Default
        }

        // Set up Drawer Toggle
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Set default fragment
        loadFragment(HomeFragment())

        // Handle Bottom Navigation
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_write -> {
                    loadFragment(WriteStoryFragment())
                    true
                }
                R.id.nav_saved_stories -> {
                    loadFragment(SavedStoriesFragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }


        // Handle Side Navigation
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    // Function to load fragments
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
