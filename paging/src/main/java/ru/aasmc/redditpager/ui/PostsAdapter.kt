package ru.aasmc.redditpager.ui

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.aasmc.redditpager.GlideRequests
import ru.aasmc.redditpager.model.RedditPost

class PostsAdapter(
    private val glide: GlideRequests
): PagingDataAdapter<RedditPost, RedditPostViewHolder>(POST_COMPARATOR) {

    override fun onBindViewHolder(holder: RedditPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: RedditPostViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val item = getItem(position)
            holder.updateScore(item)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedditPostViewHolder {
        return RedditPostViewHolder.create(parent, glide)
    }

    companion object {
        private val PAYLOAD_SCORE = Any()
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<RedditPost>() {
            override fun areContentsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean {
                return oldItem.name == newItem.name
            }

            override fun getChangePayload(oldItem: RedditPost, newItem: RedditPost): Any? {
                return if (sameExceptScore(oldItem, newItem)) {
                    PAYLOAD_SCORE
                } else {
                    null
                }
            }
        }

        private fun sameExceptScore(oldItem: RedditPost, newItem: RedditPost) : Boolean {
            // DON'T do this copy in a real app, it is just convenient here for the demo :)
            // because reddit randomizes scores, we want to pass it as a payload to minimize
            // UI updates between refreshes
            return oldItem.copy(score = newItem.score) == newItem
        }
    }
}