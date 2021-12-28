package ru.aasmc.redditpager.repo.inMemory.byItem

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import ru.aasmc.redditpager.api.RedditApi
import ru.aasmc.redditpager.model.RedditPost
import java.io.IOException

/**
 * A [PagingSource] that uses the "name" field of posts as the key for next/prev pages.
 *
 * Note that this is not the correct consumption of the Reddit API but rather shown here as an
 * alternative implementation which might be more suitable for your backend.
 *
 * @see [PageKeyedSubredditPagingSource]
 */
class ItemKeyedSubredditPagingSource(
    private val redditApi: RedditApi,
    private val subredditName: String
): PagingSource<String, RedditPost>() {

    override fun getRefreshKey(state: PagingState<String, RedditPost>): String? {
        /**
         * The name field is a unique identifier for post items.
         * https://www.reddit.com/dev/api
         */
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.name
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RedditPost> {
        return try {
            val items = redditApi.getTop(
                subreddit = subredditName,
                after = if (params is LoadParams.Append) params.key else null,
                before = if (params is LoadParams.Prepend) params.key else null,
                limit = params.loadSize
            ).data.children.map { it.data }

            LoadResult.Page(
                data = items,
                prevKey = items.firstOrNull()?.name,
                nextKey = items.lastOrNull()?.name
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}