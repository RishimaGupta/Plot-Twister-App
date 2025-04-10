package com.example.plottwisterapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class WriteStoryFragment : Fragment() {
    private lateinit var editTextStoryTitle: EditText
    private lateinit var editTextStoryContent: EditText
    private lateinit var seekBarFontSize: SeekBar
    private lateinit var btnSetReminder: Button
    private lateinit var tvStopwatch: TextView
    private lateinit var btnStartStopwatch: Button
    private lateinit var btnResetStopwatch: Button
    private lateinit var btnWordCount: Button
    private lateinit var tvWordCount: TextView
    private lateinit var btnSaveStory: Button
    private lateinit var btnClearStory: Button
    private lateinit var btnShowStory: Button
    private lateinit var btnShareStory: Button
    private lateinit var sharedPreferences: SharedPreferences

    private var isRunning = false
    private var elapsedTime = 0
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_write_story, container, false)

        editTextStoryTitle = view.findViewById(R.id.editTextStoryTitle)
        editTextStoryContent = view.findViewById(R.id.editTextStoryContent)
        seekBarFontSize = view.findViewById(R.id.seekBarFontSize)
        btnSetReminder = view.findViewById(R.id.btnSetReminder)
        tvStopwatch = view.findViewById(R.id.tvStopwatch)
        btnStartStopwatch = view.findViewById(R.id.btnStartStopwatch)
        btnResetStopwatch = view.findViewById(R.id.btnResetStopwatch)
        btnWordCount = view.findViewById(R.id.btnWordCount)
        tvWordCount = view.findViewById(R.id.tvWordCount)
        btnSaveStory = view.findViewById(R.id.btnSaveStory)
        btnClearStory = view.findViewById(R.id.btnClearStory)
        btnShowStory = view.findViewById(R.id.btnShowStory)
        btnShareStory = view.findViewById(R.id.btnShareStory)

        sharedPreferences = requireContext().getSharedPreferences("SavedStories", Context.MODE_PRIVATE)

        btnShowStory.visibility = View.GONE

        btnSaveStory.setOnClickListener { saveStory() }
        btnClearStory.setOnClickListener { clearFields() }

        btnShowStory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SavedStoriesFragment())
                .addToBackStack(null)
                .commit()

            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNav.selectedItemId = R.id.nav_saved_stories
        }

        seekBarFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                editTextStoryContent.textSize = progress.toFloat()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        btnSetReminder.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()

            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth/${month + 1}/$year"

                TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                    val selectedTime = String.format("%02d:%02d", hourOfDay, minute)

                    Toast.makeText(requireContext(), "Reminder set for $selectedDate at $selectedTime", Toast.LENGTH_LONG).show()

                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }


        btnStartStopwatch.setOnClickListener {
            if (!isRunning) {
                isRunning = true
                handler.post(object : Runnable {
                    override fun run() {
                        elapsedTime++
                        val minutes = elapsedTime / 60
                        val seconds = elapsedTime % 60
                        tvStopwatch.text = String.format("%02d:%02d", minutes, seconds)
                        handler.postDelayed(this, 1000)
                    }
                })
            }
        }

        btnResetStopwatch.setOnClickListener {
            isRunning = false
            elapsedTime = 0
            tvStopwatch.text = "00:00"
            handler.removeCallbacksAndMessages(null)
        }

        btnWordCount.setOnClickListener {
            val words = editTextStoryContent.text.toString().trim()
                .split("\\s+".toRegex())
                .filter { it.isNotEmpty() }
            tvWordCount.text = "Word Count: ${words.size}"
        }
        btnShareStory.setOnClickListener { view ->
            val popup = PopupMenu(requireContext(), view)
            popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_share -> {
                        shareStory()
                        true
                    }
                    R.id.action_export -> {
                        exportStory()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        return view
    }

    private fun saveStory() {
        val title = editTextStoryTitle.text.toString().trim()
        val content = editTextStoryContent.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(requireContext(), "Title and Story cannot be empty!", Toast.LENGTH_SHORT).show()
            return
        }

        val timestamp = System.currentTimeMillis().toString()
        sharedPreferences.edit().putString(timestamp, "$title\n\n$content").apply()

        Toast.makeText(requireContext(), "Story Saved!", Toast.LENGTH_SHORT).show()
        btnClearStory.visibility = View.GONE
        btnShowStory.visibility = View.VISIBLE
    }

    private fun clearFields() {
        editTextStoryTitle.text.clear()
        editTextStoryContent.text.clear()
        Toast.makeText(requireContext(), "Cleared", Toast.LENGTH_SHORT).show()
    }
    private fun shareStory() {
        val title = editTextStoryTitle.text.toString().trim()
        val content = editTextStoryContent.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(requireContext(), "Nothing to share!", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }

    private fun exportStory() {
        val title = editTextStoryTitle.text.toString().trim()
        val content = editTextStoryContent.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(requireContext(), "Nothing to export!", Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(requireContext(), "Story exported!", Toast.LENGTH_SHORT).show()
    }
}