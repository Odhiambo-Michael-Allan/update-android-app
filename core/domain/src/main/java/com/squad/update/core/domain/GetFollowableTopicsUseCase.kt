package com.squad.update.core.domain

import com.squad.update.core.data.repository.TopicsRepository
import com.squad.update.core.data.repository.UserDataRepository
import com.squad.update.core.model.data.FollowableTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * A use case which obtains a list of topics with their followed state.
 */
class GetFollowableTopicsUseCase @Inject constructor(
    private val topicsRepository: TopicsRepository,
    private val userDataRepository: UserDataRepository
) {

    /**
     * Returns a list of topics with their associated followed state.
     *
     * @param sortBy - the field used to sort the topics. Default NONE = no sorting.
     */
    operator fun invoke( sortBy: TopicSortField = TopicSortField.NONE): Flow<List<FollowableTopic>> = combine(
        userDataRepository.userData,
        topicsRepository.getTopics()
    ) { userData, topics ->
        val followedTopics = topics
            .map { topic ->
                FollowableTopic(
                    topic = topic,
                    isFollowed = topic.id in userData.followedTopics
                )
            }
        when ( sortBy ) {
            TopicSortField.NAME -> followedTopics.sortedBy { it.topic.name }
            else -> followedTopics
        }
    }
}

enum class TopicSortField {
    NONE,
    NAME
}