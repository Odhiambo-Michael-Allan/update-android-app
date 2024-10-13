package com.squad.update.core.network

import com.squad.update.core.network.model.NetworkChangeList
import com.squad.update.core.network.model.NetworkNewsResource
import com.squad.update.core.network.model.NetworkTopic

/**
 * Interface representing network calls to the Update backend.
 */
interface UpdateNetworkDataSource {
    suspend fun getTopics( ids: List<String>? = null ): List<NetworkTopic>
    suspend fun getNewsResources( ids: List<String>? = null ): List<NetworkNewsResource>
    suspend fun getTopicChangeList( after: Int? = null ): List<NetworkChangeList>
    suspend fun getNewsResourceChangeList( after: Int? = null ): List<NetworkChangeList>
}