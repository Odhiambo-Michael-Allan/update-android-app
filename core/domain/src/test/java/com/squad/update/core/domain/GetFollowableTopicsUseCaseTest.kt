package com.squad.update.core.domain

import com.squad.update.core.model.data.FollowableTopic
import com.squad.update.core.model.data.Topic
import com.squad.update.core.testing.repository.TestTopicsRepository
import com.squad.update.core.testing.repository.TestUserDataRepository
import com.squad.update.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class GetFollowableTopicsUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val topicsRepository = TestTopicsRepository()
    private val userDataRepository = TestUserDataRepository()

    val useCase = GetFollowableTopicsUseCase(
        topicsRepository = topicsRepository,
        userDataRepository = userDataRepository
    )

    @Test
    fun whenNoParams_followableTopicsAreReturnedWithNoSorting() = runTest {
        // Obtain a stream of followable topics.
        val followableTopics = useCase()

        // Send some test topics and their followed state.
        topicsRepository.sendTopics( testTopics )
        userDataRepository.setFollowedTopicIds( setOf( testTopics[0].id, testTopics[2].id ) )

        // Check that the order hasn't changed and that the correct topics are marked as followed.
        assertEquals(
            listOf(
                FollowableTopic( testTopics[0], true ),
                FollowableTopic( testTopics[1], false ),
                FollowableTopic( testTopics[2], true )
            ),
            followableTopics.first()
        )
    }

}

private val testTopics = listOf(
    Topic( "1", "Headlines", "", "", "", "" ),
    Topic( "2", "Android Studio", "", "", "", "" ),
    Topic( "3", "Compose", "", "", "", "" )
)