package com.example.plottwisterapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var tvUserName: TextView
    private lateinit var profileImage: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        // Initialize views
        profileImage = findViewById(R.id.profile_image)
        tvUserName = findViewById(R.id.tvUserName)
        logoutButton = findViewById(R.id.btn_logout)

        val isGuest = sharedPreferences.getBoolean("isGuest", false)
        val userName = sharedPreferences.getString("loggedInUser", "Guest")

        if (isGuest) {
            tvUserName.text = "Welcome, Guest!"
            logoutButton.text = "Exit Guest Mode"
        } else {
            tvUserName.text = "Welcome, $userName!"
        }

        // Handle logout button click
        logoutButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.clear() // Clears all saved user data
            editor.apply()

            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
