package com.squad.update.core.data.repository.impl

import com.squad.update.core.data.repository.NewsRepository
import com.squad.update.core.data.repository.NewsResourceQuery
import com.squad.update.core.data.repository.UserDataRepository
import com.squad.update.core.data.repository.UserNewsResourceRepository
import com.squad.update.core.model.data.UserNewsResource
import com.squad.update.core.model.data.mapToUserNewsResources
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implements a [UserNewsResourceRepository] by combining a [NewsRepository] with a
 * [UserDataRepository].
 */
@OptIn( ExperimentalCoroutinesApi::class )
class CompositeUserNewsResourceRepository @Inject constructor(
    val newsRepository: NewsRepository,
    val userDataRepository: UserDataRepository
) : UserNewsResourceRepository {

    /**
     * Returns available news resources ( joined with user data ) matching the given query
     */
    override fun observeAll( query: NewsResourceQuery ): Flow<List<UserNewsResource>> =
        newsRepository.getNewsResources( query )
            .combine( userDataRepository.userData ) { newsResources, userData ->
                newsResources.mapToUserNewsResources( userData )
            }

    /**
     * Returns available news resources ( joined with user data ) for the followed topics.
     */
    override fun observeAllForFollowedTopics(): Flow<List<UserNewsResource>> =
        userDataRepository.userData.map { it.followedTopics }.distinctUntilChanged()
            .flatMapLatest { followedTopics ->
                when {
                    followedTopics.isEmpty() -> flowOf( emptyList() )
                    else -> observeAll( NewsResourceQuery( filterTopicIds = followedTopics ) )
                }
            }

    override fun observeAllBookmarked(): Flow<List<UserNewsResource>> =
        userDataRepository.userData.map { it.bookmarkedNewsResources }.distinctUntilChanged()
            .flatMapLatest { bookmarkedNewsResources ->
                when {
                    bookmarkedNewsResources.isEmpty() -> flowOf( emptyList() )
                    else -> observeAll( NewsResourceQuery( filterNewsIds = bookmarkedNewsResources ) )
                }
            }
}