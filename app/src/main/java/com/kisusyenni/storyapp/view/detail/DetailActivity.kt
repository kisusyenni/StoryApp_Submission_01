package com.kisusyenni.storyapp.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kisusyenni.storyapp.R
import com.kisusyenni.storyapp.data.source.local.entity.Story
import com.kisusyenni.storyapp.databinding.ActivityDetailBinding
import com.kisusyenni.storyapp.helper.formatDate

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setDetailData()
    }

    private fun setDetailData() {
        val story = intent.getParcelableExtra<Story>(DETAIL_STORY) as Story
        binding.tvDetailName.text = story.name
        binding.tvDetailCreatedAt.text = story.createdAt?.let { formatDate(it) }
        binding.tvDetailDescription.text = story.description
        Glide.with(this@DetailActivity)
            .load(story.photoUrl)
            .placeholder(R.drawable.preview)
            .error(R.drawable.image_error)
            .into(binding.ivDetailPhoto)
    }

    companion object {
        const val DETAIL_STORY = "detail_story"
    }
}