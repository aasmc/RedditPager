package ru.aasmc.redditpager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "posts",
    indices = [Index(value = ["subreddit"], unique = false)])
data class RedditPost(
    @PrimaryKey
    @SerializedName("name")
    val name: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("score")
    val score: Int,
    @SerializedName("author")
    val author: String,
    @SerializedName("subreddit")
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val subreddit: String,
    @SerializedName("num_comments")
    val numComments: Int,
    @SerializedName("created_utc")
    val created: Long,
    val thumbnail: String?,
    val url: String?
) {
    // to be consistent w/ changing backend order, we need to keep a data like this
    var indexInResponse: Int = -1
}
