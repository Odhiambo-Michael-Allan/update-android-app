package com.squad.update.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.squad.update.core.database.dao.NewsResourceDao
import com.squad.update.core.database.dao.TopicDao
import com.squad.update.core.database.model.NewsResourceEntity
import com.squad.update.core.database.model.NewsResourceTopicCrossRef
import com.squad.update.core.database.model.TopicEntity
import com.squad.update.core.database.model.asExternalModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import org.junit.Test

class NewsResourceDaoTest {

    private lateinit var newsResourceDao: NewsResourceDao
    private lateinit var topicDao: TopicDao
    private lateinit var db: UpdateDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            UpdateDatabase::class.java
        ).build()
        newsResourceDao = db.newsResourceDao()
        topicDao = db.topicDao()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun newsResourceDao_fetches_items_by_descending_publish_date() = runTest {
        val newsResourceEntities = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0,
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3
            ),
            testNewsResource(
                id  = "2",
                millisSinceEpoch = 1
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2
            )
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities
        )

        val savedNewsResourceEntities = newsResourceDao.getNewsResources().first()

        assertEquals(
            listOf( 3L, 2L, 1L, 0L ),
            savedNewsResourceEntities.map {
                it.asExternalModel().publishDate.toEpochMilliseconds()
            }
        )
    }

    @Test
    fun newsResourceDao_filters_items_by_news_ids_by_descending_publish_date() = runTest {
        val newsResourceEntities = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0,
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2
            )
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities
        )

        val savedNewsResourceEntities = newsResourceDao.getNewsResources(
            useFilterNewsIds = true,
            filterNewsIds = setOf( "3", "0" )
        ).first()

        assertEquals(
            listOf( "3", "0" ),
            savedNewsResourceEntities.map { it.entity.id }
        )
    }

    @Test
    fun newsResourceDao_filters_items_by_topic_ids_by_descending_publish_date() = runTest {
        val topicEntities = listOf(
            testTopicEntity(
                id = "1",
                name = "1"
            ),
            testTopicEntity(
                id = "2",
                name = "2"
            )
        )
        val newsResourceEntities = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2
            )
        )
        val newsResourceTopicCrossRefEntities = topicEntities.mapIndexed { index, topicEntity ->
            NewsResourceTopicCrossRef(
                newsResourceId = index.toString(),
                topicId = topicEntity.id
            )
        }

        topicDao.insertOrIgnoreTopics(
            topicEntities = topicEntities
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities
        )
        newsResourceDao.insertOrIgnoreTopicCrossRefEntities(
            newsResourceTopicCrossRefEntities
        )

        val filteredNewsResources = newsResourceDao.getNewsResources(
            useFilterTopicIds = true,
            filterTopicIds = topicEntities
                .map( TopicEntity::id )
                .toSet()
        ).first()

        assertEquals(
            listOf( "1", "0" ),
            filteredNewsResources.map { it.entity.id }
        )
    }

    @Test
    fun newsResourceDao_filters_items_by_news_and_topic_ids_by_descending_publish_date() = runTest {
        val topicEntities = listOf(
            testTopicEntity(
                id = "1",
                name = "1"
            ),
            testTopicEntity(
                id = "2",
                name = "2"
            ),
            testTopicEntity(
                id = "3",
                name = "3"
            )
        )
        val newsResourceEntities = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2
            )
        )
        val newsResourceTopicCrossRefEntities = topicEntities.mapIndexed { index, topicEntity ->
            NewsResourceTopicCrossRef(
                newsResourceId = index.toString(),
                topicId = topicEntity.id
            )
        }

        topicDao.insertOrIgnoreTopics(
            topicEntities = topicEntities
        )
        newsResourceDao.upsertNewsResources(
            newsResourceEntities = newsResourceEntities
        )
        newsResourceDao.insertOrIgnoreTopicCrossRefEntities(
            newsResourceTopicCrossReferences = newsResourceTopicCrossRefEntities
        )

        val filteredNewsResources = newsResourceDao.getNewsResources(
            useFilterTopicIds = true,
            filterTopicIds = topicEntities
                .map( TopicEntity::id )
                .toSet(),
            useFilterNewsIds = true,
            filterNewsIds = setOf( "0", "1" )
        ).first()

        assertEquals(
            listOf( "1", "0" ),
            filteredNewsResources.map { it.entity.id }
        )
    }

    @Test
    fun newsResourceDao_deletes_items_by_ids() = runTest {
        val newsResourceEntities = listOf(
            testNewsResource(
                id = "0",
                millisSinceEpoch = 0
            ),
            testNewsResource(
                id = "1",
                millisSinceEpoch = 3
            ),
            testNewsResource(
                id = "2",
                millisSinceEpoch = 1
            ),
            testNewsResource(
                id = "3",
                millisSinceEpoch = 2
            )
        )
        newsResourceDao.upsertNewsResources( newsResourceEntities )

        val ( toDelete, toKeep ) = newsResourceEntities.partition { it.id.toInt() % 2 == 0 }

        newsResourceDao.deleteNewsResources(
            toDelete.map( NewsResourceEntity::id )
        )

        assertEquals(
            toKeep.map( NewsResourceEntity::id ).toSet(),
            newsResourceDao.getNewsResources().first()
                .map { it.entity.id }
                .toSet()
        )
    }
}

private fun testNewsResource(
    id: String = "0",
    millisSinceEpoch: Long = 0
) = NewsResourceEntity(
    id = id,
    title = "",
    content = "",
    url = "",
    headerImageUrl = "",
    publishDate = Instant.fromEpochMilliseconds( millisSinceEpoch ),
    type = "Article 📚",
)

private fun testTopicEntity(
    id: String = "0",
    name: String
) = TopicEntity(
    id = id,
    name = name,
    shortDescription = "",
    longDescription = "",
    url = "",
    imageUrl = ""
)