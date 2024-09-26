package com.squad.update.core.data.repository

import com.squad.update.core.data.Syncable
import com.squad.update.core.model.data.Topic
import kotlinx.coroutines.flow.Flow

interface TopicsRepository : Syncable {
    /**
     * Gets the available topics as a stream
     */
    fun getTopics(): Flow<List<Topic>>

    /**
     * Gets data for a specific topic
     */
    fun getTopic( id: String ): Flow<Topic>
}