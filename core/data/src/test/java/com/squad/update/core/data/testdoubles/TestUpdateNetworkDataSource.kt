package com.squad.update.core.data.testdoubles

import com.squad.update.core.network.UpdateNetworkDataSource
import com.squad.update.core.network.demo.DemoUpdateNetworkDataSource
import com.squad.update.core.network.model.NetworkChangeList
import com.squad.update.core.network.model.NetworkNewsResource
import com.squad.update.core.network.model.NetworkTopic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.serialization.json.Json

enum class CollectionType {
    Topics,
    NewsResources,
}

@OptIn( ExperimentalCoroutinesApi::class )
class TestUpdateNetworkDataSource : UpdateNetworkDataSource {

    private val source = DemoUpdateNetworkDataSource(
        UnconfinedTestDispatcher(),
        Json { ignoreUnknownKeys = true }
    )

    private val allTopics = runBlocking { source.getTopics() }
    private val allNewsResources = runBlocking { source.getNewsResources() }

    private val changeLists: MutableMap<CollectionType, List<NetworkChangeList>> = mutableMapOf(
        CollectionType.Topics to allTopics.mapToChangeList( idGetter = NetworkTopic::id ),
        CollectionType.NewsResources to allNewsResources.mapToChangeList(
            idGetter = NetworkNewsResource::id
        )
    )

    override suspend fun getTopics( ids: List<String>? ): List<NetworkTopic> =
        allTopics.matchIds(
            ids = ids,
            idGetter = NetworkTopic::id
        )

    override suspend fun getNewsResources( ids: List<String>? ): List<NetworkNewsResource> =
        allNewsResources.matchIds(
            ids = ids,
            idGetter = NetworkNewsResource::id,
        )

    override suspend fun getTopicChangeList( after: Int? ): List<NetworkChangeList> =
        changeLists.getValue( CollectionType.Topics ).after( after )

    override suspend fun getNewsResourceChangeList( after: Int? ): List<NetworkChangeList> =
        changeLists.getValue( CollectionType.NewsResources ).after( after )

    fun latestChangeListVersion( collectionType: CollectionType ) =
        changeLists.getValue( collectionType ).last().changeListVersion

    /**
     * Edits the change list for the backing [collectionType] for the given [id] mimicking the
     * server's change list registry.
     */
    fun editCollection( collectionType: CollectionType, id: String, isDelete: Boolean ) {
        val changeList = changeLists.getValue( collectionType )
        val latestVersion = changeList.lastOrNull()?.changeListVersion ?: 0
        val change = NetworkChangeList(
            id = id,
            isDelete = isDelete,
            changeListVersion = latestVersion + 1,
        )
        changeLists[collectionType] = changeList.filterNot { it.id == id } + change
    }

    fun changeListAfter( collectionType: CollectionType, version: Int ) =
        changeLists.getValue( collectionType ).after( version )
}

fun List<NetworkChangeList>.after( version: Int? ): List<NetworkChangeList> = when ( version ) {
    null -> this
    else -> filter { it.changeListVersion > version }
}

/**
 * Return items from [this] whose id defined by [idGetter] is in [ids] if [ids] is not null
 */
private fun <T> List<T>.matchIds(
    ids: List<String>?,
    idGetter: (T) -> String,
) = when ( ids ) {
    null -> this
    else -> ids.toSet().let { idSet -> filter { idGetter( it ) in idSet } }
}

private fun <T> List<T>.mapToChangeList(
    idGetter: (T) -> String
) = mapIndexed { index, item ->
    NetworkChangeList(
        id = idGetter( item ),
        changeListVersion = index + 1,
        isDelete = false
    )
}
