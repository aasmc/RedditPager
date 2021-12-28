package ru.aasmc.redditpager.repo.inMemory.byPage

import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import ru.aasmc.redditpager.api.RedditApi
import ru.aasmc.redditpager.model.RedditPost
import java.io.IOException

class PageKeyedSubredditPagingSource(
    private val redditApi: RedditApi,
    private val subredditName: String
): PagingSource<String, RedditPost>() {

    override fun getRefreshKey(state: PagingState<String, RedditPost>): String? {
        return state.anchorPosition?.let { position ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
            state.closestPageToPosition(position)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, RedditPost> {
        return try {
            val data = redditApi.getTop(
                subreddit = subredditName,
                after = if (params is LoadParams.Append) params.key else null,
                before = if (params is LoadParams.Prepend) params.key else null,
                limit = params.loadSize
            ).data

            LoadResult.Page(
                data = data.children.map { it.data },
                prevKey = data.before,
                nextKey = data.after
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}