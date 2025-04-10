package com.example.plottwisterapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class SettingsFragment : Fragment() {
    private lateinit var switchDarkMode: Switch
    private lateinit var switchAutoSave: Switch
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        switchDarkMode = view.findViewById(R.id.switchDarkMode)
        switchAutoSave = view.findViewById(R.id.switchAutoSave)

        sharedPreferences = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

        // Load saved preferences
        switchDarkMode.isChecked = sharedPreferences.getBoolean("DarkMode", false)
        switchAutoSave.isChecked = sharedPreferences.getBoolean("AutoSave", true)

        // Toggle Dark Mode
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("DarkMode", isChecked).apply()
            applyDarkMode(isChecked)
        }

        // Toggle AutoSave
        switchAutoSave.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("AutoSave", isChecked).apply()
            Toast.makeText(requireContext(), "Auto-Save ${if (isChecked) "Enabled" else "Disabled"}", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun applyDarkMode(enabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (enabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
