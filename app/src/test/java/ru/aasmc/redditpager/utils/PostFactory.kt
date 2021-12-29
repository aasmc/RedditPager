package ru.aasmc.redditpager.utils

import ru.aasmc.redditpager.model.RedditPost
import java.util.concurrent.atomic.AtomicInteger

class PostFactory {
    private val counter = AtomicInteger(0)
    fun createRedditPost(subredditName : String) : RedditPost {
        val id = counter.incrementAndGet()
        val post = RedditPost(
            name = "name_$id",
            title = "title $id",
            score = 10,
            author = "author $id",
            numComments = 0,
            created = System.currentTimeMillis(),
            thumbnail = null,
            subreddit = subredditName,
            url = null
        )
        post.indexInResponse = -1
        return post
    }
}