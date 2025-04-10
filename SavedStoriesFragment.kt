package com.example.plottwisterapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SavedStoriesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StoryAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editStoryLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_saved_stories, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewStories)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        editStoryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val updatedTitle = data?.getStringExtra("updatedTitle") ?: return@registerForActivityResult
                val updatedContent = data.getStringExtra("updatedContent") ?: return@registerForActivityResult
                val position = data.getIntExtra("position", -1)

                if (position != -1) {
                    adapter.updateStory(position, updatedTitle, updatedContent)
                    saveStoriesToSharedPreferences()
                }
            }
        }

        sharedPreferences = requireContext().getSharedPreferences("SavedStories", Context.MODE_PRIVATE)

        val stories = loadStoriesFromSharedPreferences()

        adapter = StoryAdapter(stories)
        recyclerView.adapter = adapter

        return view
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        requireActivity().menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position = item.groupId
        if (position < 0 || position >= adapter.getStoryList().size) return false

        when (item.itemId) {
            101 -> editStory(position)
            102 -> deleteStory(position)
        }
        return super.onContextItemSelected(item)
    }

    private fun editStory(position: Int) {
        val story = adapter.getStoryList()[position]
        val editIntent = Intent(requireContext(), EditStoryActivity::class.java).apply {
            putExtra("title", story.title)
            putExtra("content", story.content)
            putExtra("position", position)
        }
        editStoryLauncher.launch(editIntent)
    }

    private fun deleteStory(position: Int) {
        val storyList = adapter.getStoryList()
        if (position < 0 || position >= storyList.size) return

        val storyToDelete = storyList[position]

        val allKeysBefore = sharedPreferences.all.keys
        println("Before Deletion: $allKeysBefore")

        // Remove from SharedPreferences
        val editor = sharedPreferences.edit()
        editor.remove(storyToDelete.title).commit()

        // 🔹 DEBUG: Print stored keys after deleting
        val allKeysAfter = sharedPreferences.all.keys
        println("After Deletion: $allKeysAfter")

        // Remove from RecyclerView
        adapter.removeStory(position)

        Toast.makeText(requireContext(), "Story deleted", Toast.LENGTH_SHORT).show()
    }

    private fun loadStoriesFromSharedPreferences(): MutableList<Story> {
        val stories = mutableListOf<Story>()
        for ((key, value) in sharedPreferences.all) {
            val content = value.toString()
            stories.add(Story(key, content))
        }
        return stories
    }

    private fun saveStoriesToSharedPreferences() {
        val editor = sharedPreferences.edit()
        editor.clear() // Clear existing data

        for (story in adapter.getStoryList()) {
            editor.putString(story.title, story.content)
        }

        editor.apply()
    }

}
