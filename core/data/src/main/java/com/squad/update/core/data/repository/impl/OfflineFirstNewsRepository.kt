package com.squad.update.core.data.repository.impl

import com.squad.update.core.data.Synchronizer
import com.squad.update.core.data.changeListSync
import com.squad.update.core.data.model.asEntity
import com.squad.update.core.data.model.topicCrossReferences
import com.squad.update.core.data.model.topicEntityShells
import com.squad.update.core.data.repository.NewsRepository
import com.squad.update.core.data.repository.NewsResourceQuery
import com.squad.update.core.database.dao.NewsResourceDao
import com.squad.update.core.database.dao.TopicDao
import com.squad.update.core.database.model.PopulatedNewsResource
import com.squad.update.core.database.model.TopicEntity
import com.squad.update.core.database.model.asExternalModel
import com.squad.update.core.datastore.ChangeListVersions
import com.squad.update.core.datastore.UpdatePreferencesDataSource
import com.squad.update.core.model.data.NewsResource
import com.squad.update.core.network.UpdateNetworkDataSource
import com.squad.update.core.network.model.NetworkNewsResource
import com.squad.update.notifications.Notifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstNewsRepository @Inject constructor(
    private val updatePreferencesDataSource: UpdatePreferencesDataSource,
    private val newsResourceDao: NewsResourceDao,
    private val topicDao: TopicDao,
    private val network: UpdateNetworkDataSource,
    private val notifier: Notifier
) : NewsRepository {

    override fun getNewsResources(
        query: NewsResourceQuery
    ): Flow<List<NewsResource>> = newsResourceDao.getNewsResources(
        useFilterTopicIds = query.filterTopicIds != null,
        filterTopicIds = query.filterTopicIds ?: emptySet(),
        useFilterNewsIds = query.filterNewsIds != null,
        filterNewsIds = query.filterNewsIds ?: emptySet()
    ).map { it.map( PopulatedNewsResource::asExternalModel ) }


    override suspend fun syncWith( synchronizer: Synchronizer ): Boolean {
        var isFirstSync = false
        return synchronizer.changeListSync(
            versionReader = ChangeListVersions::newsResourceVersion,
            changeListFetcher = { currentVersion ->
                isFirstSync = currentVersion <= 0
                network.getNewsResourceChangeList( after = currentVersion )
            },
            versionUpdater = { latestVersion ->
                copy( newsResourceVersion = latestVersion )
            },
            modelDeleter = newsResourceDao::deleteNewsResources,
            modelUpdater = { changedIds ->
                val userData = updatePreferencesDataSource.userData.first()
                val shouldHideTopicSelection = userData.shouldHideTopicSelection
                val followedTopicIds = userData.followedTopics

                val existingNewsResourceIdsThatHaveChanged = when {
                    shouldHideTopicSelection -> newsResourceDao.getNewsResourceIds(
                        useFilterTopicIds = true,
                        filterTopicIds = followedTopicIds,
                        useFilterNewsIds = true,
                        filterNewsIds = changedIds.toSet()
                    ).first().toSet()
                    // No need to retrieve anything if notifications won't be sent.
                    else -> emptySet()
                }
                if ( isFirstSync ) {
                    // When we first retrieve news, mark everything viewed, so that we aren't
                    // overwhelmed with all historical news.
                    updatePreferencesDataSource.setNewsResourcesViewed( changedIds, true )
                }

                // Obtain the news resources which have changed from the network and upsert them
                // locally.
                changedIds.chunked( SYNC_BATCH_SIZE ).forEach { chunkedIds ->
                    val networkNewsResources = network.getNewsResources( ids = chunkedIds )

                    // Order of invocation matters to satisfy id and foreign key constrains!
                    topicDao.insertOrIgnoreTopics(
                        topicEntities = networkNewsResources
                            .map( NetworkNewsResource::topicEntityShells )
                            .flatten()
                            .distinctBy( TopicEntity::id )
                    )
                    newsResourceDao.upsertNewsResources(
                        newsResourceEntities = networkNewsResources.map(
                            NetworkNewsResource::asEntity
                        )
                    )
                    newsResourceDao.insertOrIgnoreTopicCrossRefEntities(
                        newsResourceTopicCrossReferences = networkNewsResources
                            .map( NetworkNewsResource::topicCrossReferences )
                            .distinct()
                            .flatten()
                    )
                }
                if ( shouldHideTopicSelection ) {
                    val addedNewsResources = newsResourceDao.getNewsResources(
                        useFilterTopicIds = true,
                        filterTopicIds = followedTopicIds,
                        useFilterNewsIds = true,
                        filterNewsIds = changedIds.toSet() - existingNewsResourceIdsThatHaveChanged
                    ).first().map( PopulatedNewsResource::asExternalModel )

                    if ( addedNewsResources.isNotEmpty() ) {
                        notifier.postNewNotifications(
                            newsResources = addedNewsResources
                        )
                    }
                }
            }
        )
    }
}

// Heuristic value to optimize for serialization and deserialization cost on client and server
// for each news resource batch.
private const val SYNC_BATCH_SIZE = 40

