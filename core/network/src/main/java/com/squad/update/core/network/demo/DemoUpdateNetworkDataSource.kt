package com.squad.update.core.network.demo

import JvmUnitTestDemoAssetManager
import com.squad.update.core.common.network.Dispatcher
import com.squad.update.core.common.network.UpdateDispatchers
import com.squad.update.core.network.UpdateNetworkDataSource
import com.squad.update.core.network.model.NetworkChangeList
import com.squad.update.core.network.model.NetworkNewsResource
import com.squad.update.core.network.model.NetworkTopic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

/**
 * [UpdateNetworkDataSource] implementation that provides static news resources to aid development.
 */
class DemoUpdateNetworkDataSource @Inject constructor(
    @Dispatcher( UpdateDispatchers.IO ) private val ioDispatcher: CoroutineDispatcher,
    private val networkJson: Json,
    private val assets: DemoAssetManager = JvmUnitTestDemoAssetManager
) : UpdateNetworkDataSource {

    @OptIn( ExperimentalSerializationApi::class )
    override suspend fun getTopics(ids: List<String>? ): List<NetworkTopic> =
        withContext( ioDispatcher ) {
            assets.open( TOPICS_ASSET ).use( networkJson::decodeFromStream )
        }

    @OptIn( ExperimentalSerializationApi::class )
    override suspend fun getNewsResources(ids: List<String>?): List<NetworkNewsResource> =
        withContext( ioDispatcher ) {
            assets.open( NEWS_ASSET ).use( networkJson::decodeFromStream )
        }

    override suspend fun getTopicChangeList( after: Int? ): List<NetworkChangeList> =
        getTopics().mapToChangeList( NetworkTopic::id )

    override suspend fun getNewsResourceChangeList( after: Int? ): List<NetworkChangeList> =
        getNewsResources().mapToChangeList( NetworkNewsResource::id )

    companion object {
        private const val NEWS_ASSET = "news.json"
        private const val TOPICS_ASSET = "topics.json"
    }
}

/**
 * Converts a list of [T] to change list of all the items in it where [idGetter] defines the
 * [NetworkChangeList.id]
 */
private fun <T> List<T>.mapToChangeList(
    idGetter: ( T ) -> String,
) = mapIndexed { index, item ->
    NetworkChangeList(
        id = idGetter( item ),
        changeListVersion = index,
        isDelete = false
    )
}