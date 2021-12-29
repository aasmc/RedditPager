package ru.aasmc.redditpager

import android.app.Application
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import ru.aasmc.redditpager.api.RedditApi
import ru.aasmc.redditpager.repo.RedditPostRepository
import ru.aasmc.redditpager.ui.RedditActivity
import ru.aasmc.redditpager.ui.SubredditViewModel.Companion.DEFAULT_SUBREDDIT
import ru.aasmc.redditpager.utils.FakeRedditApiAndroidTest
import ru.aasmc.redditpager.utils.PostFactoryAndroidTest

@RunWith(Parameterized::class)
class RedditActivityTest(
    private val type: RedditPostRepository.Type
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun params() = arrayOf(RedditPostRepository.Type.IN_MEMORY_BY_ITEM,
            RedditPostRepository.Type.IN_MEMORY_BY_PAGE
        )
    }
    private val postFactory = PostFactoryAndroidTest()

    @Before
    fun init() {
        val fakeApi = FakeRedditApiAndroidTest()
        fakeApi.addPost(postFactory.createRedditPost(DEFAULT_SUBREDDIT))
        fakeApi.addPost(postFactory.createRedditPost(DEFAULT_SUBREDDIT))
        fakeApi.addPost(postFactory.createRedditPost(DEFAULT_SUBREDDIT))
        val app = ApplicationProvider.getApplicationContext<Application>()
        // use a controlled service locator w/ fake API
        ServiceLocator.swap(
            object : DefaultServiceLocator(app = app, useInMemoryDb = true) {
                override fun getRedditApi(): RedditApi = fakeApi
            }
        )
    }

    @Test
    fun showSomeResults() {
        ActivityScenario.launch<RedditActivity>(
            RedditActivity.intentFor(
                context = ApplicationProvider.getApplicationContext(),
                type = type
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )

        Espresso.onView(withId(R.id.list)).check { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            val recyclerView = view as RecyclerView
            Assert.assertEquals(3, recyclerView.adapter?.itemCount)
        }
    }

}

























