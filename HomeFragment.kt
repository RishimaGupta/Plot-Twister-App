package com.example.plottwisterapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.* 
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private lateinit var tvWelcome: TextView
    private lateinit var btnGeneratePlot: Button
    private lateinit var tvGeneratedPlot: TextView
    private lateinit var btnNext: Button
    private lateinit var cbSupernatural: CheckBox
    private lateinit var btnMystery: Button
    private lateinit var btnSciFi: Button
    private lateinit var btnFantasy: Button
    private lateinit var btnHorror: Button
    private lateinit var btnRomance: Button

    private var selectedGenre: String? = null
    private var generatedPlot: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize UI elements
        tvWelcome = view.findViewById(R.id.tvWelcome)
        btnGeneratePlot = view.findViewById(R.id.btnGeneratePlot)
        tvGeneratedPlot = view.findViewById(R.id.tvGeneratedPlot)
        btnNext = view.findViewById(R.id.btnNext)
        cbSupernatural = view.findViewById(R.id.cbSupernatural)

        btnMystery = view.findViewById(R.id.btnMystery)
        btnSciFi = view.findViewById(R.id.btnSciFi)
        btnFantasy = view.findViewById(R.id.btnFantasy)
        btnHorror = view.findViewById(R.id.btnHorror)
        btnRomance = view.findViewById(R.id.btnRomance)

        // Random Story Plots categorized by Genre
        val plots = mapOf(
            "Mystery" to listOf(
                "A detective investigating a haunted mansion...",
                "A stranger leaves a cryptic message in a diary...",
                "A crime scene with no visible entry or exit..."
            ),
            "Sci-Fi" to listOf(
                "A scientist discovers a portal to another dimension...",
                "A rogue AI takes control of a spaceship...",
                "A cyber-enhanced detective uncovers a government conspiracy..."
            ),
            "Fantasy" to listOf(
                "A wizard uncovers a forbidden spell...",
                "A dragon egg hatches in an unexpected place...",
                "A magical sword chooses an unlikely hero..."
            ),
            "Horror" to listOf(
                "A haunted doll starts whispering secrets...",
                "A group of friends enter an abandoned asylum...",
                "A cursed videotape dooms whoever watches it..."
            ),
            "Romance" to listOf(
                "Two strangers meet on a train and fall in love...",
                "A love letter from the past changes the present...",
                "A famous author falls for their biggest fan..."
            )
        )
        // Genre Selection Logic (Highlight selected genre)
        val genreButtons = listOf(btnMystery, btnSciFi, btnFantasy, btnHorror, btnRomance)

        genreButtons.forEach { button ->
            button.setOnClickListener {
                selectedGenre = button.text.toString()
                genreButtons.forEach { it.setBackgroundResource(android.R.drawable.btn_default) } // Reset all buttons
                button.setBackgroundResource(android.R.color.holo_blue_light) // Highlight selected
            }
        }

        // Generate Plot based on Selected Genre
        btnGeneratePlot.setOnClickListener {
            if (selectedGenre == null) {
                Toast.makeText(requireContext(), "Please select a genre!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            generatedPlot = plots[selectedGenre]?.random() ?: "Something mysterious is happening..."
            if (cbSupernatural.isChecked) {
                generatedPlot += " But there's a supernatural element involved!"
            }

            tvGeneratedPlot.text = generatedPlot
            Toast.makeText(requireContext(), "Plot generated!", Toast.LENGTH_SHORT).show()
        }

        // Navigate to Next Activity
        btnNext.setOnClickListener {
            val writeStoryFragment = WriteStoryFragment()
            val bundle = Bundle()
            bundle.putString("PLOT", generatedPlot)
            writeStoryFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, writeStoryFragment)
                .addToBackStack(null)
                .commit()
            val bottomNav =
                requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNav.selectedItemId = R.id.nav_write
        }


        val sharedPreferences =
            requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val isGuest = sharedPreferences.getBoolean("isGuest", false)
        if (isGuest) {
            btnNext.visibility = View.GONE
            Toast.makeText(requireContext(), "Limited access: You cannot write a story!", Toast.LENGTH_LONG).show()
        }
        return view
    }
}
