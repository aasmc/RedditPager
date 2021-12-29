package ru.aasmc.redditpager.utils

import retrofit2.http.Path
import retrofit2.http.Query
import ru.aasmc.redditpager.api.RedditApi
import ru.aasmc.redditpager.model.RedditPost
import java.io.IOException
import kotlin.math.min

class FakeRedditApiAndroidTest : RedditApi {
    private val model = mutableMapOf<String, SubReddit>()

    var failureMsg: String? = null
    fun addPost(post: RedditPost) {
        val subreddit = model.getOrPut(post.subreddit) {
            SubReddit(items = arrayListOf())
        }
        subreddit.items.add(post)
    }

    private fun findPosts(
        subreddit: String,
        limit: Int,
        after: String? = null,
        before: String? = null
    ): List<RedditApi.RedditChildrenResponse> {
        // only support paging forward
        if (before != null) return emptyList()
        val sub = findSubreddit(subreddit)
        val posts = sub.findPosts(limit, after)
        return posts.map { RedditApi.RedditChildrenResponse(it.copy()) }
    }

    private fun findSubreddit(subreddit: String) =
        model.getOrDefault(subreddit, SubReddit())

    override suspend fun getTop(
        @Path("subreddit") subreddit: String,
        @Query("limit") limit: Int,
        @Query("after") after: String?,
        @Query("before") before: String?
    ): RedditApi.ListingResponse {
        failureMsg?.let {
            throw IOException(it)
        }
        val items = findPosts(subreddit, limit, after, before)
        val nextAfter = items.lastOrNull()?.data?.name
        return RedditApi.ListingResponse(
            RedditApi.ListingData(
                children = items,
                after = nextAfter,
                before = null
            )
        )
    }

    private class SubReddit(val items: MutableList<RedditPost> = arrayListOf()) {
        fun findPosts(limit: Int, after: String?): List<RedditPost> {
            if (after == null) {
                return items.subList(0, min(items.size, limit))
            }
            val index = items.indexOfFirst { it.name == after }
            if (index == -1) {
                return emptyList()
            }
            val startPos = index + 1
            return items.subList(startPos, min(items.size, limit + startPos))
        }
    }
}