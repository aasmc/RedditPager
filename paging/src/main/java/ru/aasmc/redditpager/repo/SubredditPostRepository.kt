package ru.aasmc.redditpager.repo

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.aasmc.redditpager.model.RedditPost

/**
 * Common interface shared by the different repository implementations.
 * Note: this only exists for sample purposes - typically an app would implement a repo once, either
 * network+db, or network-only
 */
interface SubredditPostRepository {
    fun postsOfSubreddit(subreddit: String, pageSize: Int): Flow<PagingData<RedditPost>>

    enum class Type {
        IN_MEMORY_BY_ITEM,
        IN_MEMORY_BY_PAGE,
        DB
    }
}