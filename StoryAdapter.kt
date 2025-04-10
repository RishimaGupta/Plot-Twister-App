package com.example.plottwisterapp

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StoryAdapter(private val stories: MutableList<Story>) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.bind(story)
    }

    override fun getItemCount(): Int = stories.size

    fun getStoryList(): MutableList<Story> {
        return stories
    }

    fun updateStory(position: Int, newTitle: String, newContent: String) {
        if (position in stories.indices) {
            stories[position] = Story(newTitle, newContent)
            notifyItemChanged(position)
        }
    }

    fun removeStory(position: Int) {
        if (position in stories.indices) {
            stories.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnCreateContextMenuListener {

        private val titleTextView: TextView = itemView.findViewById(R.id.storyTitle)
        private val contentTextView: TextView = itemView.findViewById(R.id.storyContent)

        fun bind(story: Story) {
            titleTextView.text = story.title
            contentTextView.text = story.content
        }

        init {
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add(adapterPosition, 101, 0, "Edit Story")
            menu?.add(adapterPosition, 102, 1, "Delete Story")
        }
    }
}
