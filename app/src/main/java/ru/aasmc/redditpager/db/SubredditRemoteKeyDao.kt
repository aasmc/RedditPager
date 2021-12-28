package ru.aasmc.redditpager.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.aasmc.redditpager.model.SubredditRemoteKey

@Dao
interface SubredditRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: SubredditRemoteKey)

    @Query("SELECT * FROM remote_keys WHERE subreddit = :subreddit")
    suspend fun remoteKeyByPost(subreddit: String): SubredditRemoteKey

    @Query("DELETE FROM remote_keys WHERE subreddit = :subreddit")
    suspend fun deleteBySubreddit(subreddit: String)
}