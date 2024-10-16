package com.squad.update.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squad.update.core.model.data.NewsResource
import kotlinx.datetime.Instant

/**
 * Defines an Update news resource.
 */
@Entity(
    tableName = "news_resources"
)
data class NewsResourceEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val url: String,
    @ColumnInfo( name = "header_image_url" )
    val headerImageUrl: String?,
    @ColumnInfo( name = "publish_date" )
    val publishDate: Instant,
    val type: String
)

fun NewsResourceEntity.asExternalModel() = NewsResource(
    id = id,
    title = title,
    content = content,
    url = url,
    headerImageUrl = headerImageUrl,
    publishDate = publishDate,
    type = type,
    topics = listOf()
)
