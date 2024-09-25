package com.squad.update.core.data.repository.impl

import com.squad.update.core.data.Synchronizer
import com.squad.update.core.data.repository.NewsRepository
import com.squad.update.core.data.repository.NewsResourceQuery
import com.squad.update.core.model.data.NewsResource
import com.squad.update.core.model.data.Topic
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstNewsRepository @Inject constructor() : NewsRepository {

    private val newsResourcesFlow: MutableSharedFlow<List<NewsResource>> =
        MutableSharedFlow( replay = 1, onBufferOverflow =  BufferOverflow.DROP_OLDEST )

    override fun getNewsResources( query: NewsResourceQuery ): Flow<List<NewsResource>> =
        newsResourcesFlow.map { newsResources ->
            var result = newsResources
            query.filterTopicIds?.let { filterTopicIds ->
                result = newsResources.filter {
                    it.topics.map( Topic::id ).intersect( filterTopicIds ).isNotEmpty()
                }
            }
            query.filterNewsIds?.let { filterNewsIds ->
                result = newsResources.filter { it.id in filterNewsIds }
            }
            result
        }

    override suspend fun syncWith( synchronizer: Synchronizer ) = true
}