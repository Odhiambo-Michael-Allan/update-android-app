package com.squad.update.core.data.repository.impl

import com.squad.update.core.data.Synchronizer
import com.squad.update.core.data.model.asEntity
import com.squad.update.core.data.testdoubles.CollectionType
import com.squad.update.core.data.testdoubles.TestTopicDao
import com.squad.update.core.data.testdoubles.TestUpdateNetworkDataSource
import com.squad.update.core.database.dao.TopicDao
import com.squad.update.core.database.model.TopicEntity
import com.squad.update.core.database.model.asExternalModel
import com.squad.update.core.datastore.UpdatePreferencesDataSource
import com.squad.update.core.datastore_test.testUserPreferencesDataStore
import com.squad.update.core.model.data.Topic
import com.squad.update.core.network.model.NetworkTopic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn( ExperimentalCoroutinesApi::class )
class OfflineFirstTopicsRepositoryTest {

    private val testScope = TestScope( UnconfinedTestDispatcher() )

    private lateinit var subject: OfflineFirstTopicsRepository

    private lateinit var topicDao: TopicDao

    private lateinit var network: TestUpdateNetworkDataSource

    private lateinit var updatePreferences: UpdatePreferencesDataSource

    private lateinit var synchronizer: Synchronizer

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    @Before
    fun setup() {
        topicDao = TestTopicDao()
        network = TestUpdateNetworkDataSource()
        updatePreferences = UpdatePreferencesDataSource(
            tmpFolder.testUserPreferencesDataStore( testScope.backgroundScope )
        )
        synchronizer = TestSynchronizer( updatePreferences )

        subject = OfflineFirstTopicsRepository(
            topicDao = topicDao,
            network = network
        )
    }

    @Test
    fun offlineFirstTopicsRepository_topics_stream_is_backed_by_topics_dao() =
        testScope.runTest {
            assertEquals(
                topicDao.getTopicEntities()
                    .first()
                    .map( TopicEntity::asExternalModel ),
                subject.getTopics()
                    .first()
            )
        }

    @Test
    fun offlineFirstTopicsRepository_sync_pulls_from_network() =
        testScope.runTest {
            subject.syncWith( synchronizer )

            val networkTopics = network.getTopics()
                .map( NetworkTopic::asEntity )

            val dbTopics = topicDao.getTopicEntities().first()

            assertEquals(
                networkTopics.map( TopicEntity::id ),
                dbTopics.map( TopicEntity::id )
            )

            // After sync, change list version should be updated.
            assertEquals(
                network.latestChangeListVersion( CollectionType.Topics ),
                synchronizer.getChangeListVersions().topicVersion
            )
        }

    @Test
    fun offlineFirstTopicsRepository_incremental_sync_pulls_from_network() =
        testScope.runTest {
            // Set topics version to 10
            synchronizer.updateChangeListVersions {
                copy( topicVersion = 10 )
            }

            subject.syncWith( synchronizer )

            val networkTopics = network.getTopics()
                .map( NetworkTopic::asEntity )
                // Drop 10 to simulate the first 10 items being unchanged
                .drop( 10 )
            val dbTopics = topicDao.getTopicEntities().first()

            assertEquals(
                networkTopics.map( TopicEntity::id ),
                dbTopics.map( TopicEntity::id )
            )

            // After sync version should be updated.
            assertEquals(
                network.latestChangeListVersion( CollectionType.Topics ),
                synchronizer.getChangeListVersions().topicVersion
            )
        }

    @Test
    fun offlineFirstTopicsRepository_sync_deletes_items_marked_deleted_on_network() =
        testScope.runTest {
            val networkTopics = network.getTopics()
                .map( NetworkTopic::asEntity )
                .map( TopicEntity::asExternalModel )

            // Delete half of the items on the network
            val deletedItems = networkTopics
                .map( Topic::id )
                .partition { it.chars().sum() % 2 == 0 }
                .first
                .toSet()

            deletedItems.forEach {
                network.editCollection(
                    collectionType = CollectionType.Topics,
                    id = it,
                    isDelete = true
                )
            }

            subject.syncWith( synchronizer )

            val dbTopics = topicDao.getTopicEntities()
                .first()
                .map( TopicEntity::asExternalModel )

            // Assert that items marked deleted on the network have been deleted locally
            assertEquals(
                networkTopics.map( Topic::id ) - deletedItems,
                dbTopics.map( Topic::id )
            )

            // After sync version should be updated
            assertEquals(
                network.latestChangeListVersion( CollectionType.Topics ),
                synchronizer.getChangeListVersions().topicVersion
            )
        }
}