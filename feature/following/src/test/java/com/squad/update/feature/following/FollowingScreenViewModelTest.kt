package com.squad.update.feature.following

import com.squad.update.core.data.repository.impl.CompositeUserNewsResourceRepository
import com.squad.update.core.domain.GetFollowableTopicsUseCase
import com.squad.update.core.model.data.FollowableTopic
import com.squad.update.core.model.data.NewsResource
import com.squad.update.core.model.data.Topic
import com.squad.update.core.model.data.UserNewsResource
import com.squad.update.core.model.data.mapToUserNewsResources
import com.squad.update.core.testing.repository.TestNewsRepository
import com.squad.update.core.testing.repository.TestTopicsRepository
import com.squad.update.core.testing.repository.TestUserDataRepository
import com.squad.update.core.testing.repository.emptyUserData
import com.squad.update.core.testing.util.MainDispatcherRule
import com.squad.update.core.testing.util.TestSyncManager
import com.squad.update.core.ui.NewsFeedUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Assert.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
@OptIn( ExperimentalCoroutinesApi::class)
class FollowingScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val syncManager = TestSyncManager()
    private val userDataRepository = TestUserDataRepository()
    private val topicsRepository = TestTopicsRepository()
    private val newsRepository = TestNewsRepository()
    private val userNewsResourceRepository = CompositeUserNewsResourceRepository(
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
    )

    private val getFollowableTopicsUseCase = GetFollowableTopicsUseCase(
        topicsRepository = topicsRepository,
        userDataRepository = userDataRepository,
    )

    private lateinit var viewModel: FollowingScreenViewModel

    @Before
    fun setUp() {
        viewModel = FollowingScreenViewModel(
            syncManager = syncManager,
            userDataRepository = userDataRepository,
            userNewsResourceRepository = userNewsResourceRepository,
            getFollowableTopicsUseCase = getFollowableTopicsUseCase
        )
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(
            TopicSelectionUiState.Loading,
            viewModel.topicSelectionUiState.value
        )
        assertEquals( NewsFeedUiState.Loading, viewModel.feedState.value )
    }

    @Test
    fun stateIsLoadingWhenFollowedTopicsAreLoading() = runTest {
        val collectJob1 =
            launch( UnconfinedTestDispatcher() ) { viewModel.topicSelectionUiState.collect() }
        val collectJob2 = launch( UnconfinedTestDispatcher() ) { viewModel.feedState.collect() }

        topicsRepository.sendTopics( sampleTopics )

        assertEquals(
            TopicSelectionUiState.Loading,
            viewModel.topicSelectionUiState.value
        )

        assertEquals(
            NewsFeedUiState.Loading,
            viewModel.feedState.value
        )

        collectJob1.cancel()
        collectJob2.cancel()

    }

    @Test
    fun stateIsLoadingWhenAppIsSyncingWithNoInterests() = runTest {
        val collectJob = launch( UnconfinedTestDispatcher() ) { viewModel.isSyncing.collect() }

        syncManager.setSyncing( true )

        assertEquals( true, viewModel.isSyncing.value )

        collectJob.cancel()
    }

    @Test
    fun topicSelectionUiStateIsLoadingWhenTopicsAreLoading() = runTest {
        val collectJob1 =
            launch( UnconfinedTestDispatcher() ) { viewModel.topicSelectionUiState.collect() }
        val collectJob2 = launch( UnconfinedTestDispatcher() ) { viewModel.feedState.collect() }

        userDataRepository.setFollowedTopicIds( emptySet() )

        assertEquals(
            TopicSelectionUiState.Loading,
            viewModel.topicSelectionUiState.value
        )

        assertEquals( NewsFeedUiState.Success( emptyList() ), viewModel.feedState.value )

        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun topicSelectionIsShownWhenNewsResourcesAreLoading() = runTest {
        val collectJob1 = launch( UnconfinedTestDispatcher() ) {
            viewModel.topicSelectionUiState.collect()
        }
        val collectJob2 = launch( UnconfinedTestDispatcher() ) { viewModel.feedState.collect() }

        topicsRepository.sendTopics( sampleTopics )
        userDataRepository.setFollowedTopicIds( emptySet() )

        assertEquals(
            TopicSelectionUiState.Shown(
                topics = listOf(
                    FollowableTopic(
                        topic = Topic(
                            id = "0",
                            name = "Headlines",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL"
                        ),
                        isFollowed = false,
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "1",
                            name = "UI",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL"
                        ),
                        isFollowed = false,
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "2",
                            name = "Tools",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL"
                        ),
                        isFollowed = false,
                    )
                )
            ),
            viewModel.topicSelectionUiState.value
        )

        assertEquals(
            NewsFeedUiState.Success(
                userNewsResources = emptyList()
            ),
            viewModel.feedState.value
        )

        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun topicSelectionIsShownAfterLoadingEmptyFollowedTopics() = runTest {
        val collectJob1 = launch( UnconfinedTestDispatcher() ) {
            viewModel.topicSelectionUiState.collect()
        }
        val collectJob2 = launch( UnconfinedTestDispatcher() ) { viewModel.feedState.collect() }

        topicsRepository.sendTopics( sampleTopics )
        userDataRepository.setFollowedTopicIds( emptySet() )
        newsRepository.sendNewsResources( sampleNewsResources )

        assertEquals(
            TopicSelectionUiState.Shown(
                topics = listOf(
                    FollowableTopic(
                        topic = Topic(
                            id = "0",
                            name = "Headlines",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL"
                        ),
                        isFollowed = false,
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "1",
                            name = "UI",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL"
                        ),
                        isFollowed = false
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "2",
                            name = "Tools",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL"
                        ),
                        isFollowed = false,
                    )
                )
            ),
            viewModel.topicSelectionUiState.value
        )
        assertEquals(
            NewsFeedUiState.Success(
                userNewsResources = emptyList()
            ),
            viewModel.feedState.value
        )

        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun topicSelectionIsNotShownAfterUserDismissesTopicSelection() = runTest {
        val collectJob1 = launch( UnconfinedTestDispatcher() ) {
            viewModel.topicSelectionUiState.collect()
        }
        val collectJob2 = launch( UnconfinedTestDispatcher() ) {
            viewModel.feedState.collect()
        }

        topicsRepository.sendTopics( sampleTopics )
        val followedTopicIds = setOf( "0", "1" )
        val userData = emptyUserData.copy( followedTopics = followedTopicIds )
        userDataRepository.setUserData( userData )
        viewModel.dismissTopicSelection()

        assertEquals(
            TopicSelectionUiState.NotShown,
            viewModel.topicSelectionUiState.value
        )
        assertEquals( NewsFeedUiState.Loading, viewModel.feedState.value )

        newsRepository.sendNewsResources( sampleNewsResources )

        assertEquals(
            TopicSelectionUiState.NotShown,
            viewModel.topicSelectionUiState.value
        )

        assertEquals(
            NewsFeedUiState.Success(
                userNewsResources = sampleNewsResources.mapToUserNewsResources( userData )
            ),
            viewModel.feedState.value,
        )

        collectJob1.cancel()
        collectJob2.cancel()
    }

    @Test
    fun topicSelectionUpdatesAfterSelectingTopic() = runTest {
        val collectJob1 = launch( UnconfinedTestDispatcher() ) {
            viewModel.topicSelectionUiState.collect()
        }
        val collectJob2 = launch( UnconfinedTestDispatcher() ) {
            viewModel.feedState.collect()
        }

        topicsRepository.sendTopics( sampleTopics )
        userDataRepository.setFollowedTopicIds( emptySet() )
        newsRepository.sendNewsResources( sampleNewsResources )

        assertEquals(
            TopicSelectionUiState.Shown(
                topics = sampleTopics.map {
                    FollowableTopic( it, false )
                }
            ),
            viewModel.topicSelectionUiState.value
        )

        assertEquals(
            NewsFeedUiState.Success(
                userNewsResources = emptyList()
            ),
            viewModel.feedState.value
        )

        val followedTopicId = sampleTopics[1].id
        viewModel.updateTopicSelection( followedTopicId, isChecked = true )

        assertEquals(
            TopicSelectionUiState.Shown(
                topics = sampleTopics.map {
                    FollowableTopic( it, it.id == followedTopicId )
                }
            ),
            viewModel.topicSelectionUiState.value
        )

        val userData = emptyUserData.copy( followedTopics = setOf( followedTopicId ) )

        assertEquals(
            NewsFeedUiState.Success(
                userNewsResources = listOf(
                    UserNewsResource( sampleNewsResources[1], userData ),
                    UserNewsResource( sampleNewsResources[2], userData )
                )
            ),
            viewModel.feedState.value
        )

        collectJob1.cancel()
        collectJob2.cancel()
    }
}

private val sampleTopics = listOf(
    Topic(
        id = "0",
        name = "Headlines",
        shortDescription = "",
        longDescription = "long description",
        url = "URL",
        imageUrl = "image URL",
    ),
    Topic(
        id = "1",
        name = "UI",
        shortDescription = "",
        longDescription = "long description",
        url = "URL",
        imageUrl = "image URL",
    ),
    Topic(
        id = "2",
        name = "Tools",
        shortDescription = "",
        longDescription = "long description",
        url = "URL",
        imageUrl = "image URL",
    ),
)

private val sampleNewsResources = listOf(
    NewsResource(
        id = "1",
        title = "Thanks for helping us reach 1M YouTube Subscribers",
        content = "Thank you everyone for following the Now in Android series and everything the " +
                "Android Developers YouTube channel has to offer. During the Android Developer " +
                "Summit, our YouTube channel reached 1 million subscribers! Here’s a small video to " +
                "thank you all.",
        url = "https://youtu.be/-fJ6poHQrjM",
        headerImageUrl = "https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg",
        publishDate = Instant.parse( "2021-11-09T00:00:00.000Z" ),
        type = "Video 📺",
        topics = listOf(
            Topic(
                id = "0",
                name = "Headlines",
                shortDescription = "",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            ),
        ),
    ),
    NewsResource(
        id = "2",
        title = "Transformations and customisations in the Paging Library",
        content = "A demonstration of different operations that can be performed with Paging. " +
                "Transformations like inserting separators, when to create a new pager, and " +
                "customisation options for consuming PagingData.",
        url = "https://youtu.be/ZARz0pjm5YM",
        headerImageUrl = "https://i.ytimg.com/vi/ZARz0pjm5YM/maxresdefault.jpg",
        publishDate = Instant.parse( "2021-11-01T00:00:00.000Z" ),
        type = "Video 📺",
        topics = listOf(
            Topic(
                id = "1",
                name = "UI",
                shortDescription = "",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            ),
        ),
    ),
    NewsResource(
        id = "3",
        title = "Community tip on Paging",
        content = "Tips for using the Paging library from the developer community",
        url = "https://youtu.be/r5JgIyS3t3s",
        headerImageUrl = "https://i.ytimg.com/vi/r5JgIyS3t3s/maxresdefault.jpg",
        publishDate = Instant.parse( "2021-11-08T00:00:00.000Z" ),
        type = "Video 📺",
        topics = listOf(
            Topic(
                id = "1",
                name = "UI",
                shortDescription = "",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            ),
        ),
    ),
)