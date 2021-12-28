package ru.aasmc.redditpager.repo.inMemory.byPage

import androidx.paging.Pager
import androidx.paging.PagingConfig
import ru.aasmc.redditpager.api.RedditApi
import ru.aasmc.redditpager.repo.RedditPostRepository

/**
 * Repository implementation that loads data directly from network by using the previous / next page
 * keys returned in the query.
 */
class InMemoryByPageKeyRepository(private val redditApi: RedditApi) : RedditPostRepository {
    override fun postsOfSubreddit(subreddit: String, pageSize: Int) = Pager(
        PagingConfig(pageSize)
    ) {
        PageKeyedSubredditPagingSource(
            redditApi = redditApi,
            subredditName = subreddit
        )
    }.flow
}
