package ru.aasmc.redditpager.repo.inDb

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.aasmc.redditpager.api.RedditApi
import ru.aasmc.redditpager.db.RedditDb
import ru.aasmc.redditpager.model.RedditPost
import ru.aasmc.redditpager.repo.RedditPostRepository

/**
 * Repo implementation that uses a database backed Pagingsource
 * and RemoteMediator to load pages from network when there are no
 * more items cached in the database to load.
 */
class DbRedditPostRepository(
    val db: RedditDb,
    val redditApi: RedditApi
): RedditPostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun postsOfSubreddit(subreddit: String, pageSize: Int): Flow<PagingData<RedditPost>> {
        return Pager(
            config = PagingConfig(pageSize),
            remoteMediator = PageKeyedRemoteMediator(db, redditApi, subreddit)
        ) {
            db.posts().postsBySubreddit(subreddit)
        }.flow
    }
}