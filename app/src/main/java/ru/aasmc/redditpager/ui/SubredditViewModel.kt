package ru.aasmc.redditpager.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import ru.aasmc.redditpager.repo.RedditPostRepository

class SubredditViewModel(
    private val repository: RedditPostRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        const val KEY_SUBREDDIT = "subreddit"
        const val DEFAULT_SUBREDDIT = "androiddev"
    }

    init {
        if (!savedStateHandle.contains(KEY_SUBREDDIT)) {
            savedStateHandle.set(KEY_SUBREDDIT, DEFAULT_SUBREDDIT)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts = savedStateHandle.getLiveData<String>(KEY_SUBREDDIT)
        .asFlow()
        .flatMapLatest { repository.postsOfSubreddit(it, 30) }
        // cachedIn() shares the paging state across multiple consumers of posts,
        // e.g. different generations of UI across rotation config change
        .cachedIn(viewModelScope)

    fun showSubreddit(subreddit: String) {
        if (!shouldShowSubreddit(subreddit)) return
        savedStateHandle.set(KEY_SUBREDDIT, subreddit)
    }

    private fun shouldShowSubreddit(subreddit: String): Boolean {
        return savedStateHandle.get<String>(KEY_SUBREDDIT) != subreddit
    }
}