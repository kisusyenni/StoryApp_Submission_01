package com.kisusyenni.storyapp.view.home

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kisusyenni.storyapp.R
import com.kisusyenni.storyapp.data.source.local.entity.Story
import com.kisusyenni.storyapp.data.source.remote.response.ListStoryItem
import com.kisusyenni.storyapp.databinding.ItemStoryBinding
import com.kisusyenni.storyapp.helper.formatDate
import com.kisusyenni.storyapp.view.detail.DetailActivity

class HomeAdapter :
    ListAdapter<ListStoryItem, HomeAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val itemStoryBinding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(itemStoryBinding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)

        if (story != null) {
            holder.bind(story)
        }
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            with(binding) {
                tvItemName.text = story.name
                tvItemCreatedAt.text = formatDate(story.createdAt)
                tvItemDescription.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.preview)
                    .error(R.drawable.image_error)
                    .into(ivItemPhoto)
                val (photoUrl, createdAt, name, description, lon, id, lat) = story
                val selectedStory = Story(
                    photoUrl,
                    createdAt,
                    name,
                    description,
                    lon,
                    id,
                    lat
                )

                itemView.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(ivItemPhoto, "image"),
                            Pair(tvItemName, "name"),
                            Pair(tvItemCreatedAt, "created_at"),
                            Pair(tvItemDescription, "description"),
                        )
                    val detailIntent = Intent(itemView.context, DetailActivity::class.java)
                    detailIntent.putExtra(DetailActivity.DETAIL_STORY, selectedStory)
                    itemView.context.startActivity(detailIntent, optionsCompat.toBundle())
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}