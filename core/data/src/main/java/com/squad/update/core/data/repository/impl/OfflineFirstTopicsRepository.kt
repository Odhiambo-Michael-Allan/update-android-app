package com.squad.update.core.data.repository.impl

import com.squad.update.core.data.Synchronizer
import com.squad.update.core.data.repository.TopicsRepository
import com.squad.update.core.model.data.Topic
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Disk storage backed implementation of the [TopicsRepository].
 * Reads are exclusively from local storage to support offline
 * access.
 */
class OfflineFirstTopicsRepository @Inject constructor() : TopicsRepository {

    /**
     * The backing hot flow for the list of topic ids for testing.
     */
    private val topicsFlow: MutableSharedFlow<List<Topic>> =
        MutableSharedFlow( replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST )

    override fun getTopics(): Flow<List<Topic>> = topicsFlow

    override fun getTopic( id: String ): Flow<Topic> =
        topicsFlow.map { topics -> topics.find { it.id == id }!! }

    override suspend fun syncWith( synchronizer: Synchronizer ) = true


}