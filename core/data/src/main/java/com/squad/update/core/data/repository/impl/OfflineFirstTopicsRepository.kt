package com.squad.update.core.data.repository.impl

import com.squad.update.core.data.Synchronizer
import com.squad.update.core.data.repository.TopicsRepository
import com.squad.update.core.model.data.NewsResource
import com.squad.update.core.model.data.Topic
import com.squad.update.core.model.data.UserNewsResource
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
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

    init {
        topicsFlow.tryEmit( topics )
    }

    override fun getTopics(): Flow<List<Topic>> = topicsFlow

    override fun getTopic( id: String ): Flow<Topic> =
        topicsFlow.map { topics -> topics.find { it.id == id }!! }

    override suspend fun syncWith( synchronizer: Synchronizer ) = true

}


internal val topics = listOf(
    Topic(
        id = "2",
        name = "Headlines",
        shortDescription = "News we want everyone to see",
        longDescription = "Stay up to date with the latest events and announcements from Android!",
        imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Headlines.svg?alt=media&token=506faab0-617a-4668-9e63-4a2fb996603f",
        url = "",
    ),
    Topic(
        id = "3",
        name = "UI",
        shortDescription = "Material Design, Navigation, Text, Paging, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets",
        longDescription = "Learn how to optimize your app's user interface - everything that users can see and interact with. Stay up to date on topics such as Material Design, Navigation, Text, Paging, Compose, Accessibility (a11y), Internationalization (i18n), Localization (l10n), Animations, Large Screens, Widgets, and many more!",
        imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_UI.svg?alt=media&token=0ee1842b-12e8-435f-87ba-a5bb02c47594",
        url = "",
    ),
    Topic(
        id = "4",
        name = "Testing",
        shortDescription = "CI, Espresso, TestLab, etc",
        longDescription = "Testing is an integral part of the app development process. By running tests against your app consistently, you can verify your app's correctness, functional behavior, and usability before you release it publicly. Stay up to date on the latest tricks in CI, Espresso, and Firebase TestLab.",
        imageUrl = "https://firebasestorage.googleapis.com/v0/b/now-in-android.appspot.com/o/img%2Fic_topic_Testing.svg?alt=media&token=a11533c4-7cc8-4b11-91a3-806158ebf428",
        url = "",
    ),
)
