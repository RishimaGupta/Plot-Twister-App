package com.example.plottwisterapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditStoryActivity : AppCompatActivity() {
    private lateinit var etTitle: EditText
    private lateinit var etContent: EditText
    private lateinit var btnSave: Button
    private lateinit var sharedPreferences: SharedPreferences

    private var position: Int = -1 // Position of the story in the list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_story)

        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        btnSave = findViewById(R.id.btnSave)

        sharedPreferences = getSharedPreferences("SavedStories", Context.MODE_PRIVATE)

        // Retrieve data from Intent
        val title = intent.getStringExtra("title") ?: ""
        val content = intent.getStringExtra("content") ?: ""
        position = intent.getIntExtra("position", -1)

        // Set data to EditText fields
        etTitle.setText(title)
        etContent.setText(content)

        // Save the edited story
        btnSave.setOnClickListener {
            saveEditedStory()
        }
    }

    private fun saveEditedStory() {
        val newTitle = etTitle.text.toString().trim()
        val newContent = etContent.text.toString().trim()

        if (newTitle.isEmpty() || newContent.isEmpty()) {
            Toast.makeText(this, "Title and content cannot be empty!", Toast.LENGTH_SHORT).show()
            return
        }

        val editor = sharedPreferences.edit()
        val key = sharedPreferences.all.keys.elementAt(position)

        // Save updated story
        editor.putString(key, "$newTitle\n\n$newContent")
        editor.apply()

        // Send result back to fragment
        val resultIntent = Intent()
        resultIntent.putExtra("updatedTitle", newTitle)
        resultIntent.putExtra("updatedContent", newContent)
        resultIntent.putExtra("position", position)
        setResult(Activity.RESULT_OK, resultIntent)

        Toast.makeText(this, "Story updated successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
