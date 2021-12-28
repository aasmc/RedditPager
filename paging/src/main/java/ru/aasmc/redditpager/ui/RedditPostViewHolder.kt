package ru.aasmc.redditpager.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.aasmc.paging.R
import ru.aasmc.paging.databinding.RedditPostItemBinding
import ru.aasmc.redditpager.GlideRequests
import ru.aasmc.redditpager.model.RedditPost

class RedditPostViewHolder(
    private val binding: RedditPostItemBinding,
    private val glide: GlideRequests
) : RecyclerView.ViewHolder(binding.root) {
    private var post: RedditPost? = null

    init {
        binding.root.setOnClickListener { view ->
            post?.url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                view.context.startActivity(intent)
            }
        }
    }

    fun bind(post: RedditPost?) {
        this.post = post
        binding.title.text = post?.title ?: "loading"
        binding.subtitle.text = binding.root.context.resources.getString(
            R.string.post_subtitle, post?.author ?: "Unknown"
        )
        binding.score.text = "${post?.score ?: 0}"
        if (post?.thumbnail?.startsWith("http") == true) {
            binding.thumbnail.visibility = View.VISIBLE
            glide.load(post.thumbnail)
                .centerCrop()
                .placeholder(R.drawable.ic_insert_photo_black_48dp)
                .into(binding.thumbnail)
        } else {
            binding.thumbnail.visibility = View.GONE
            glide.clear(binding.thumbnail)
        }
    }

    fun updateScore(item: RedditPost?) {
        post = item
        binding.score.text = "${item?.score ?: 0}"
    }

    companion object {
        fun create(parent: ViewGroup, glide: GlideRequests): RedditPostViewHolder {
            val binding = RedditPostItemBinding.bind(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.reddit_post_item, parent, false)
            )
            return RedditPostViewHolder(binding, glide)
        }
    }
}