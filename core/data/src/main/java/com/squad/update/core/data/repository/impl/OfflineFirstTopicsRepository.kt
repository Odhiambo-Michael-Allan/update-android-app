package com.squad.update.core.data.repository.impl

import com.squad.update.core.data.Synchronizer
import com.squad.update.core.data.changeListSync
import com.squad.update.core.data.model.asEntity
import com.squad.update.core.data.repository.TopicsRepository
import com.squad.update.core.database.dao.TopicDao
import com.squad.update.core.database.model.TopicEntity
import com.squad.update.core.database.model.asExternalModel
import com.squad.update.core.datastore.ChangeListVersions
import com.squad.update.core.model.data.NewsResource
import com.squad.update.core.model.data.Topic
import com.squad.update.core.model.data.UserNewsResource
import com.squad.update.core.network.UpdateNetworkDataSource
import com.squad.update.core.network.model.NetworkTopic
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import javax.inject.Inject

/**
 * Disk storage backed implementation of the [TopicsRepository].
 * Reads are exclusively from local storage to support offline
 * access.
 */
class OfflineFirstTopicsRepository @Inject constructor(
    private val topicDao: TopicDao,
    private val network: UpdateNetworkDataSource
) : TopicsRepository {

    override fun getTopics(): Flow<List<Topic>> =
        topicDao.getTopicEntities()
            .map { it.map( TopicEntity::asExternalModel ) }

    override fun getTopic( id: String ): Flow<Topic> =
        topicDao.getTopicEntity( id ).map { it.asExternalModel() }

    override suspend fun syncWith( synchronizer: Synchronizer ): Boolean =
        synchronizer.changeListSync(
            versionReader = ChangeListVersions::topicVersion,
            changeListFetcher = { currentVersion ->
                network.getTopicChangeList( after = currentVersion )
            },
            versionUpdater = { latestVersion ->
                copy( topicVersion = latestVersion )
            },
            modelDeleter = topicDao::deleteTopics,
            modelUpdater = { changedIds ->
                val networkTopics = network.getTopics( ids = changedIds )
                topicDao.upsertTopics(
                    entities = networkTopics.map( NetworkTopic::asEntity )
                )
            }
        )

}
