package com.example.plottwisterapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val rbRememberMe = findViewById<RadioButton>(R.id.rbRememberMe)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignup = findViewById<TextView>(R.id.tvSignup)
        val btnGuest = findViewById<Button>(R.id.btnGuest)

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)



        // Check if "Remember Me" was selected before
        if (sharedPreferences.getBoolean("rememberMe", false)) {
            etEmail.setText(sharedPreferences.getString("savedEmail", ""))
            etPassword.setText(sharedPreferences.getString("savedPassword", ""))
            rbRememberMe.isChecked = true
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val savedPassword = sharedPreferences.getString(email, null)

            val editor = sharedPreferences.edit()
            editor.putBoolean("isGuest", false)
            editor.putString("loggedInUser", email)
            editor.apply()

            if (savedPassword != null && savedPassword == password) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                if (rbRememberMe.isChecked) {
                    editor.putBoolean("rememberMe", true)
                    editor.putString("savedEmail", email)
                    editor.putString("savedPassword", password)
                }
                else {
                    editor.remove("rememberMe")
                    editor.remove("savedEmail")
                    editor.remove("savedPassword")
                }
                editor.apply()

                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("USER_TYPE", "User")
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }

        tvSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        btnGuest.setOnClickListener {
            Toast.makeText(this, "Continuing as Guest", Toast.LENGTH_SHORT).show()
            val editor = sharedPreferences.edit()
            editor.putBoolean("isGuest", true) // Mark the user as Guest
            editor.apply()

            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("USER_TYPE", "Guest")
            startActivity(intent)
            finish()
        }
    }
}

