package ru.aasmc.redditpager.repo.inMemory.byItem

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.aasmc.redditpager.api.RedditApi
import ru.aasmc.redditpager.model.RedditPost
import ru.aasmc.redditpager.repo.RedditPostRepository

/**
 * Repository implementation that that loads data directly from the
 * network and uses the Item's name
 * as the key to discover prev/next pages.
 */
class InMemoryByItemRepository(
    private val redditApi: RedditApi
): RedditPostRepository {

    override fun postsOfSubreddit(subreddit: String, pageSize: Int): Flow<PagingData<RedditPost>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            )
        ) {
            ItemKeyedSubredditPagingSource(
                redditApi = redditApi,
                subredditName = subreddit
            )
        }.flow
    }
}