package ru.aasmc.redditpager.repository

import androidx.paging.PagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test
import ru.aasmc.redditpager.repo.inMemory.byItem.ItemKeyedSubredditPagingSource
import ru.aasmc.redditpager.repo.inMemory.byPage.PageKeyedSubredditPagingSource
import ru.aasmc.redditpager.ui.SubredditViewModel.Companion.DEFAULT_SUBREDDIT
import ru.aasmc.redditpager.utils.FakeRedditApi
import ru.aasmc.redditpager.utils.PostFactory
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SubredditPagingSourceTest {
    private val postFactory = PostFactory()
    private val fakePosts = listOf(
        postFactory.createRedditPost(DEFAULT_SUBREDDIT),
        postFactory.createRedditPost(DEFAULT_SUBREDDIT),
        postFactory.createRedditPost(DEFAULT_SUBREDDIT)
    )

    private val fakeApi = FakeRedditApi().apply {
        fakePosts.forEach { post -> addPost(post) }
    }

    @Test
    fun itemKeyedSubredditPagingSource() = runBlocking{
        val pagingSource = ItemKeyedSubredditPagingSource(fakeApi, DEFAULT_SUBREDDIT)
        assertEquals(
            expected = PagingSource.LoadResult.Page(
                data = listOf(fakePosts[0], fakePosts[1]),
                prevKey = fakePosts[0].name,
                nextKey = fakePosts[1].name
            ),
            actual = pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            ),
        )
    }

    @Test
    fun pageKeyedSubredditPagingSource() = runBlocking {
        val pagingSource = PageKeyedSubredditPagingSource(fakeApi, DEFAULT_SUBREDDIT)
        assertEquals(
            expected = PagingSource.LoadResult.Page(
                data = listOf(fakePosts[0], fakePosts[1]),
                prevKey = null,
                nextKey = fakePosts[1].name
            ),
            actual = pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            ),
        )
    }

}
































