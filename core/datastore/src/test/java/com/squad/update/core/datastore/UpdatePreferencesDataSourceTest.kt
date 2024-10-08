package com.squad.update.core.datastore

import androidx.datastore.core.DataStore
import com.squad.update.core.datastore_test.testUserPreferencesDataStore
import com.squad.update.core.model.data.DarkThemeConfig
import com.squad.update.core.model.data.ThemeBrand
import com.squad.update.core.model.data.UserData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import javax.inject.Inject

@OptIn( ExperimentalCoroutinesApi::class )
class UpdatePreferencesDataSourceTest {

    private val testScope = TestScope( UnconfinedTestDispatcher() )

    private lateinit var subject: UpdatePreferencesDataSource

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    @Before
    fun setup() {
        subject = UpdatePreferencesDataSource(
            tmpFolder.testUserPreferencesDataStore( testScope.backgroundScope )
        )
    }

    @Test
    fun shouldHideTopicSelectionIsFalseByDefault() = testScope.runTest {
        assertFalse( subject.userData.first().shouldHideTopicSelection )
    }

    @Test
    fun shouldHideTopicSelectionIsTrueWhenSet() = testScope.runTest {
        subject.setShouldHideTopicSelection( true )
        assertTrue( subject.userData.first().shouldHideTopicSelection )
    }

    @Test
    fun userShouldHideTopicSelection_unfollowsLastTopic_shouldHideTopicSelectionIsFalse() =
        testScope.runTest {
            // Given: user selects a single topic.
            subject.setTopicIdFollowed( "1", true )
            subject.setShouldHideTopicSelection( true )

            // When: they unfollow that topic.
            subject.setTopicIdFollowed( "1", false )

            // Then: topic selection should be shown again
            assertFalse( subject.userData.first().shouldHideTopicSelection )
        }

    @Test
    fun shouldHideTopicSelection_unfollowsAllTopics_shouldHideTopicSelectionIsFalse() =
        testScope.runTest {
            // Given: user selects several topics.
            subject.setFollowedTopicIds( setOf( "1", "2" ) )
            subject.setShouldHideTopicSelection( true )

            // When: they unfollow those topics.
            subject.setFollowedTopicIds( emptySet() )

            // Then: topic selection should be shown again
            assertFalse( subject.userData.first().shouldHideTopicSelection )
        }

    @Test
    fun shouldUseDynamicColorFalseByDefault() = testScope.runTest {
        assertFalse( subject.userData.first().useDynamicColor )
    }

    @Test
    fun shouldUseDynamicColorIsTrueWhenSet() = testScope.runTest {
        subject.setDynamicColorPreference( true )
        assertTrue( subject.userData.first().useDynamicColor )
    }

    @Test
    fun setDarkThemeConfigIsSetCorrectly() = testScope.runTest {
        DarkThemeConfig.entries.forEach {
            subject.setDarkThemeConfig( it )
            assertEquals(
                it,
                subject.userData.first().darkThemeConfig
            )
        }
    }

    @Test
    fun bookmarked_news_resources_set_correctly() = testScope.runTest {
        subject.setNewsResourceBookmarked( "0", true )
        subject.setNewsResourceBookmarked( "1", true )

        assertEquals(
            setOf( "0", "1" ),
            subject.userData.first().bookmarkedNewsResources
        )

        subject.setNewsResourceBookmarked( "0", false )

        assertEquals(
            setOf( "1" ),
            subject.userData.first().bookmarkedNewsResources
        )
    }

    @Test
    fun viewedNewsResourcesAreSetCorrectly() = testScope.runTest {
        subject.setNewsResourcesViewed( listOf( "0", "1", "2" ), true )
        assertEquals(
            setOf( "0", "1", "2" ),
            subject.userData.first().viewedNewsResources
        )
    }

    @Test
    fun themeBrandIsSetCorrectly() = testScope.runTest {
        ThemeBrand.entries.forEach {
            subject.setThemeBrand( it )
            assertEquals( it, subject.userData.map { it.themeBrand }.first() )
        }
    }
}