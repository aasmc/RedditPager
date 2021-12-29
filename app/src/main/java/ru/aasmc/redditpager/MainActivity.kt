package ru.aasmc.redditpager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.aasmc.redditpager.databinding.ActivityMainBinding
import ru.aasmc.redditpager.repo.RedditPostRepository
import ru.aasmc.redditpager.ui.RedditActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.withDatabase.setOnClickListener {
            show(RedditPostRepository.Type.DB)
        }
        binding.networkOnly.setOnClickListener {
            show(RedditPostRepository.Type.IN_MEMORY_BY_ITEM)
        }
        binding.networkOnlyWithPageKeys.setOnClickListener {
            show(RedditPostRepository.Type.IN_MEMORY_BY_PAGE)
        }
    }

    private fun show(type: RedditPostRepository.Type) {
        val intent = RedditActivity.intentFor(this, type)
        startActivity(intent)
    }
}