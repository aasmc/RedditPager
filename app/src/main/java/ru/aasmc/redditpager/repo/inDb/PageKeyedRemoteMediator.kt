package ru.aasmc.redditpager.repo.inDb

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import ru.aasmc.redditpager.api.RedditApi
import ru.aasmc.redditpager.db.RedditDb
import ru.aasmc.redditpager.db.RedditPostDao
import ru.aasmc.redditpager.db.SubredditRemoteKeyDao
import ru.aasmc.redditpager.model.RedditPost
import ru.aasmc.redditpager.model.SubredditRemoteKey
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PageKeyedRemoteMediator(
    private val db: RedditDb,
    private val redditApi: RedditApi,
    private val subredditName: String
) : RemoteMediator<Int, RedditPost>() {

    private val postDao: RedditPostDao = db.posts()
    private val remoteKeyDao: SubredditRemoteKeyDao = db.remoteKeys()

    override suspend fun initialize(): InitializeAction {
        // require that remote REFRESH is launched on initial load
        // and succeeds before launching remote PREPEND / APPEND.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RedditPost>
    ): MediatorResult {
        try {
            // get the closest item from PagingState that we want to load data around.
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    // query DB for SubredditRemoteKey for the subreddit
                    val remoteKey = db.withTransaction {
                        remoteKeyDao.remoteKeyByPost(subredditName)
                    }
                    // We must explicitly check if the page key is null when appending, since the
                    // Reddit API informs the end of the list by returning null for page key, but
                    // passing a null key to Reddit API will fetch the initial page.
                    if (remoteKey.nextPageKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey.nextPageKey
                }
            }

            val data = redditApi.getTop(
                subreddit = subredditName,
                after = loadKey,
                before = null,
                limit = when (loadType) {
                    LoadType.REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                }
            ).data

            val items = data.children.map {
                it.data
            }
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    postDao.deleteBySubreddit(subredditName)
                    remoteKeyDao.deleteBySubreddit(subredditName)
                }
                remoteKeyDao.insert(SubredditRemoteKey(subredditName, data.after))
                postDao.insertAll(items)
            }
            return MediatorResult.Success(endOfPaginationReached = items.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}



















