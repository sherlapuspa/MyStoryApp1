package com.dicoding.mystoryapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.adapt.ListStoryAdapt
import com.dicoding.mystoryapp.databinding.ActivityDetailStoryBinding
import com.dicoding.mystoryapp.data.ListStoryItem

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyInfo = intent.getParcelableExtra<ListStoryItem>(STORY_DATA) as ListStoryItem
        displayStoryDetails(storyInfo)

        supportActionBar?.title = getString(R.string.usersStory, storyInfo.name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private fun displayStoryDetails(storyDetails: ListStoryItem) {
        binding.apply {
            tvHeader.text = storyDetails.name
            tvDescription.text = storyDetails.description
            tvDateTime.text = ListStoryAdapt.formatDateToString(this@DetailStoryActivity, storyDetails.createdAt)
        }
        Glide.with(this)
            .load(storyDetails.photoUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(binding.imgStoryDetail)
    }

    companion object {
        const val STORY_DATA = "story_data"
    }
}