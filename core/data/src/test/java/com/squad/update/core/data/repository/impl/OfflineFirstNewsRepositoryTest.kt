package com.squad.update.core.data.repository.impl

import com.squad.update.core.data.Synchronizer
import com.squad.update.core.data.model.asEntity
import com.squad.update.core.data.model.topicCrossReferences
import com.squad.update.core.data.model.topicEntityShells
import com.squad.update.core.data.repository.NewsResourceQuery
import com.squad.update.core.data.testdoubles.CollectionType
import com.squad.update.core.data.testdoubles.TestNewsResourceDao
import com.squad.update.core.data.testdoubles.TestTopicDao
import com.squad.update.core.data.testdoubles.TestUpdateNetworkDataSource
import com.squad.update.core.data.testdoubles.filteredInterestsIds
import com.squad.update.core.data.testdoubles.nonPresentInterestsIds
import com.squad.update.core.database.model.NewsResourceEntity
import com.squad.update.core.database.model.NewsResourceTopicCrossRef
import com.squad.update.core.database.model.PopulatedNewsResource
import com.squad.update.core.database.model.TopicEntity
import com.squad.update.core.database.model.asExternalModel
import com.squad.update.core.datastore.UpdatePreferencesDataSource
import com.squad.update.core.datastore_test.testUserPreferencesDataStore
import com.squad.update.core.model.data.NewsResource
import com.squad.update.core.model.data.Topic
import com.squad.update.core.network.model.NetworkChangeList
import com.squad.update.core.network.model.NetworkNewsResource
import com.squad.update.core.testing.notifications.TestNotifier
import com.squad.update.notifications.Notifier
import junit.runner.Version.id
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.internal.runners.model.EachTestNotifier
import org.junit.rules.TemporaryFolder

@OptIn( ExperimentalCoroutinesApi::class )
class OfflineFirstNewsRepositoryTest {

    private val testScope = TestScope( UnconfinedTestDispatcher() )

    private lateinit var subject: OfflineFirstNewsRepository

    private lateinit var updatePreferencesDataSource: UpdatePreferencesDataSource

    private lateinit var newsResourceDao: TestNewsResourceDao

    private lateinit var topicDao: TestTopicDao

    private lateinit var network: TestUpdateNetworkDataSource

    private lateinit var notifier: TestNotifier

    private lateinit var synchronizer: Synchronizer

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    @Before
    fun setup() {
        updatePreferencesDataSource = UpdatePreferencesDataSource(
            tmpFolder.testUserPreferencesDataStore( testScope.backgroundScope )
        )
        newsResourceDao = TestNewsResourceDao()
        topicDao = TestTopicDao()
        network = TestUpdateNetworkDataSource()
        notifier = TestNotifier()
        synchronizer = TestSynchronizer(
            updatePreferencesDataSource
        )
        subject = OfflineFirstNewsRepository(
            updatePreferencesDataSource = updatePreferencesDataSource,
            newsResourceDao = newsResourceDao,
            topicDao = topicDao,
            network = network,
            notifier = notifier,
        )
    }

    @Test
    fun offlineFirstNewsRepository_news_resources_stream_is_backed_by_news_resource_dao() =
        testScope.runTest {
            assertEquals(
                newsResourceDao.getNewsResources()
                    .first()
                    .map( PopulatedNewsResource::asExternalModel ),
                subject.getNewsResources()
                    .first()
            )
        }

    @Test
    fun offlineFirstNewsRepository_news_resources_for_topic_is_backed_by_news_resource_dao() =
        testScope.runTest {
            assertEquals(
                newsResourceDao.getNewsResources(
                    filterTopicIds = filteredInterestsIds,
                    useFilterTopicIds = true
                ).first().map( PopulatedNewsResource::asExternalModel ),
                subject.getNewsResources(
                    query = NewsResourceQuery(
                        filterTopicIds = filteredInterestsIds
                    )
                ).first()
            )

            assertEquals(
                emptyList<NewsResource>(),
                subject.getNewsResources(
                    query = NewsResourceQuery(
                        filterTopicIds = nonPresentInterestsIds
                    )
                ).first()
            )
        }

    @Test
    fun offlineFirstNewsRepository_sync_pulls_from_network() =
        testScope.runTest {
            // User has not dismissed topic selection.
            updatePreferencesDataSource.setShouldHideTopicSelection( false )
            subject.syncWith( synchronizer )

            val newsResourcesFromNetwork = network.getNewsResources()
                .map( NetworkNewsResource::asEntity )
                .map( NewsResourceEntity::asExternalModel )

            val newsResourcesFromDatabase = newsResourceDao.getNewsResources()
                .first()
                .map( PopulatedNewsResource::asExternalModel )

            assertEquals(
                newsResourcesFromNetwork.map( NewsResource::id ).sorted(),
                newsResourcesFromDatabase.map( NewsResource::id ).sorted()
            )

            // After sync, version should be updated.
            assertEquals(
                network.latestChangeListVersion( CollectionType.NewsResources ),
                synchronizer.getChangeListVersions().newsResourceVersion
            )

            // Notifier should not have been called.
            assertTrue( notifier.addedNewsResources.isEmpty() )
        }

    @Test
    fun offlineFirstNewsRepository_sync_deletes_items_marked_deleted_on_network() =
        testScope.runTest {
            updatePreferencesDataSource.setShouldHideTopicSelection( false )

            val newsResourcesFromNetwork = network.getNewsResources()
                .map( NetworkNewsResource::asEntity )
                .map( NewsResourceEntity::asExternalModel )

            // Delete half of the items on the network
            val deletedItems = newsResourcesFromNetwork
                .map( NewsResource::id )
                .partition { it.chars().sum() % 2 == 0 }
                .first
                .toSet()

            deletedItems.forEach {
                network.editCollection(
                    collectionType = CollectionType.NewsResources,
                    id = it,
                    isDelete = true
                )
            }

            subject.syncWith( synchronizer )

            val newsResourcesFromDatabase = newsResourceDao.getNewsResources()
                .first()
                .map( PopulatedNewsResource::asExternalModel )

            // Assert that items marked deleted on the network have been deleted locally.
            assertEquals(
                ( newsResourcesFromNetwork.map( NewsResource::id ) - deletedItems ).sorted(),
                newsResourcesFromDatabase.map( NewsResource::id ).sorted()
            )

            // After sync version should be updated.
            assertEquals(
                network.latestChangeListVersion( CollectionType.NewsResources ),
                synchronizer.getChangeListVersions().newsResourceVersion
            )

            // Notifier should not have been called.
            assertTrue( notifier.addedNewsResources.isEmpty() )
        }

    @Test
    fun offlineFirstNewsRepository_incremental_sync_pulls_from_network() =
        testScope.runTest {
            updatePreferencesDataSource.setShouldHideTopicSelection( false )

            // Set news version to 7
            synchronizer.updateChangeListVersions {
                copy( newsResourceVersion = 7 )
            }

            subject.syncWith( synchronizer )

            val changeList = network.changeListAfter(
                CollectionType.NewsResources,
                version = 7,
            )

            val changeListIds = changeList
                .map( NetworkChangeList::id )
                .toSet()

            val newsResourcesFromNetwork = network.getNewsResources()
                .map( NetworkNewsResource::asEntity )
                .map( NewsResourceEntity::asExternalModel )
                .filter { it.id in changeListIds }

            val newsResourcesFromDatabase = newsResourceDao.getNewsResources()
                .first()
                .map( PopulatedNewsResource::asExternalModel )

            assertEquals(
                newsResourcesFromNetwork.map( NewsResource::id ).sorted(),
                newsResourcesFromDatabase.map( NewsResource::id ).sorted()
            )

            // After, sync version should be updated.
            assertEquals(
                changeList.last().changeListVersion,
                synchronizer.getChangeListVersions().newsResourceVersion
            )
        }

    @Test
    fun offlineFirstNewsRepository_sync_saves_shell_topic_entities() =
        testScope.runTest {
            subject.syncWith( synchronizer )

            assertEquals(
                network.getNewsResources()
                    .map( NetworkNewsResource::topicEntityShells )
                    .flatten()
                    .distinctBy( TopicEntity::id )
                    .sortedBy( TopicEntity::toString ),
                topicDao.getTopicEntities()
                    .first()
                    .sortedBy( TopicEntity::toString )
            )
        }

    @Test
    fun offlineFirstNewsRepository_sync_saves_topic_cross_references() = testScope.runTest {
        subject.syncWith( synchronizer )

        assertEquals(
            network.getNewsResources()
                .map( NetworkNewsResource::topicCrossReferences )
                .flatten()
                .distinct()
                .sortedBy( NewsResourceTopicCrossRef::toString ),
            newsResourceDao.topicCrossReferences
                .sortedBy( NewsResourceTopicCrossRef::toString )
        )
    }

    @Test
    fun offlineFirstNewsRepository_sync_marks_as_read_on_first_run() =
        testScope.runTest {
            subject.syncWith( synchronizer )

            assertEquals(
                network.getNewsResources().map { it.id }.toSet(),
                updatePreferencesDataSource.userData.first().viewedNewsResources
            )
        }

    @Test
    fun offlineFirstNewsRepository_sync_does_not_mark_as_read_on_subsequent_run() =
        testScope.runTest {
            // Pretend we already have up to change list 7
            synchronizer.updateChangeListVersions {
                copy( newsResourceVersion = 7 )
            }
            subject.syncWith( synchronizer )

            assertEquals(
                emptySet<String>(),
                updatePreferencesDataSource.userData.first().viewedNewsResources
            )
        }

    @Test
    fun offlineFirstNewsRepository_sends_notifications_for_newly_synced_news_that_is_followed() =
        testScope.runTest {
            updatePreferencesDataSource.setShouldHideTopicSelection( true )

            val networkNewsResources = network.getNewsResources()

            // Follow roughly half the topics.
            val followedTopicIds = networkNewsResources
                .flatMap( NetworkNewsResource::topicEntityShells )
                .mapNotNull { topic ->
                    when ( topic.id.chars().sum() % 2 ) {
                        0 -> topic.id
                        else -> null
                    }
                }.toSet()

            // Set followed topics
            updatePreferencesDataSource.setFollowedTopicIds( followedTopicIds )

            subject.syncWith( synchronizer )

            val followedNewsResourceIdsFromNetwork = networkNewsResources
                .filter { ( it.topics intersect followedTopicIds ).isNotEmpty() }
                .map( NetworkNewsResource::id )
                .sorted()

            // Notifier should have been called with only news resources that have topics that the
            // user follows.
            assertEquals(
                followedNewsResourceIdsFromNetwork,
                notifier.addedNewsResources
                    .first()
                    .map( NewsResource::id )
                    .sorted()
            )
        }

    @Test
    fun offlineFirstNewsRepository_does_not_send_notifications_for_existing_news_resources() =
        testScope.runTest {
            updatePreferencesDataSource.setShouldHideTopicSelection( true )

            val networkNewsResources = network.getNewsResources()
                .map( NetworkNewsResource::asEntity )

            val newsResources = networkNewsResources
                .map( NewsResourceEntity::asExternalModel )

            // Prepopulate dao with news resources.
            newsResourceDao.upsertNewsResources( networkNewsResources )

            val followedTopicIds = newsResources
                .flatMap( NewsResource::topics )
                .map( Topic::id )
                .toSet()

            // Follow all topics.
            updatePreferencesDataSource.setFollowedTopicIds( followedTopicIds )

            subject.syncWith( synchronizer )

            // Notifier should not have been called because all news resources existed previously.
            assertTrue( notifier.addedNewsResources.isEmpty() )
        }
}