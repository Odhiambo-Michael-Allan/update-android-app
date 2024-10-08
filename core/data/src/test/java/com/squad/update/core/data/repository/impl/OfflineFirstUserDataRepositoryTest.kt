package com.squad.update.core.data.repository.impl

import com.squad.update.core.datastore.UpdatePreferencesDataSource
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

@OptIn( ExperimentalCoroutinesApi::class )
class OfflineFirstUserDataRepositoryTest {

    private val testScope = TestScope( UnconfinedTestDispatcher() )

    private lateinit var subject: OfflineFirstUserDataRepository

    private lateinit var updatePreferencesDataSource: UpdatePreferencesDataSource

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    @Before
    fun setup() {
        updatePreferencesDataSource = UpdatePreferencesDataSource(
            tmpFolder.testUserPreferencesDataStore( testScope.backgroundScope )
        )
        subject = OfflineFirstUserDataRepository(
            updatePreferencesDataSource = updatePreferencesDataSource,
        )
    }

    @Test
    fun offlineFirstUserDataRepository_default_user_data_is_correct() =
        testScope.runTest {
            assertEquals(
                UserData(
                    bookmarkedNewsResources = emptySet(),
                    viewedNewsResources = emptySet(),
                    followedTopics = emptySet(),
                    themeBrand = ThemeBrand.DEFAULT,
                    darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
                    useDynamicColor = false,
                    shouldHideTopicSelection = false,
                ),
                subject.userData.first()
            )
        }

    @Test
    fun offlineFirstUserDataRepository_toggle_followed_topics_logic_delegates_to_update_preferences() =
        testScope.runTest {
            subject.setTopicIdFollowed( followedTopicId = "0", true )

            assertEquals(
                setOf( "0" ),
                subject.userData
                    .map { it.followedTopics }
                    .first()
            )

            subject.setTopicIdFollowed( followedTopicId = "1", followed = true )

            assertEquals(
                setOf( "0", "1" ),
                subject.userData
                    .map { it.followedTopics }
                    .first()
            )

            assertEquals(
                updatePreferencesDataSource.userData
                    .map { it.followedTopics }
                    .first(),
                subject.userData
                    .map { it.followedTopics }
                    .first()
            )
        }

    @Test
    fun offlineFirstUserDataRepository_set_followed_topics_logic_delegates_to_update_preferences() =
        testScope.runTest {
            subject.setFollowedTopicIds( followedTopicIds = setOf( "1", "2" ) )

            assertEquals(
                setOf( "1", "2" ),
                subject.userData
                    .map { it.followedTopics }
                    .first()
            )

            assertEquals(
                updatePreferencesDataSource.userData
                    .map { it.followedTopics }
                    .first(),
                subject.userData
                    .map { it.followedTopics }
                    .first()
            )
        }

    @Test
    fun offlineFirstUserDataRepository_bookmark_news_resource_logic_delegates_to_update_preferences() =
        testScope.runTest {
            subject.setNewsResourceBookmarked( newsResourceId = "0", bookmarked = true )

            assertEquals(
                setOf( "0" ),
                subject.userData
                    .map { it.bookmarkedNewsResources }
                    .first()
            )

            subject.setNewsResourceBookmarked( newsResourceId = "1", bookmarked = true )

            assertEquals(
                setOf( "0", "1" ),
                subject.userData
                    .map { it.bookmarkedNewsResources }
                    .first()
            )

            assertEquals(
                updatePreferencesDataSource.userData
                    .map { it.bookmarkedNewsResources }
                    .first(),
                subject.userData
                    .map { it.bookmarkedNewsResources }
                    .first()
            )
        }

    @Test
    fun offlineFirstUserDataRepository_update_viewed_news_resources_delegates_to_update_preferences() =
        runTest {
            subject.setNewsResourceViewed( newsResourceId = "0", viewed = true )

            assertEquals(
                setOf( "0" ),
                subject.userData
                    .map { it.viewedNewsResources }
                    .first()
            )

            subject.setNewsResourceViewed( newsResourceId = "1", viewed = true )

            assertEquals(
                setOf( "0", "1" ),
                subject.userData
                    .map { it.viewedNewsResources }
                    .first()
            )

            assertEquals(
                updatePreferencesDataSource.userData
                    .map { it.viewedNewsResources }
                    .first(),
                subject.userData
                    .map { it.viewedNewsResources }
                    .first()
            )
        }

    @Test
    fun offlineFirstUserDataRepository_set_theme_brand_delegates_to_update_preferences() =
        testScope.runTest {
            subject.setThemeBrand( ThemeBrand.ANDROID )

            assertEquals(
                ThemeBrand.ANDROID,
                subject.userData
                    .map { it.themeBrand }
                    .first()
            )
            assertEquals(
                ThemeBrand.ANDROID,
                updatePreferencesDataSource
                    .userData
                    .map { it.themeBrand }
                    .first()
            )
        }

    @Test
    fun offlineFirstUserDataRepository_set_dynamic_color_delegates_to_update_preferences() =
        testScope.runTest {
            subject.setDynamicColorPreference( true )

            assertEquals(
                true,
                subject.userData
                    .map { it.useDynamicColor }
                    .first()
            )
            assertEquals(
                true,
                updatePreferencesDataSource
                    .userData
                    .map { it.useDynamicColor }
                    .first()
            )
        }

    @Test
    fun offlineFirstUserDataRepository_set_dark_theme_config_delegates_to_update_preferences() =
        testScope.runTest {
            subject.setDarkThemeConfig( DarkThemeConfig.DARK )

            assertEquals(
                DarkThemeConfig.DARK,
                subject.userData
                    .map { it.darkThemeConfig }
                    .first()
            )
            assertEquals(
                DarkThemeConfig.DARK,
                updatePreferencesDataSource
                    .userData
                    .map { it.darkThemeConfig }
                    .first()
            )
        }

    @Test
    fun whenUserCompletesTopicSelection_thenRemovesAllFollowedTopics_shouldHideTopicSelectionIsFalse() =
        testScope.runTest {
            subject.setFollowedTopicIds( setOf( "1" ) )
            subject.setShouldHideTopicSelection( true )
            assertTrue( subject.userData.first().shouldHideTopicSelection )

            subject.setFollowedTopicIds( emptySet() )
            assertFalse( subject.userData.first().shouldHideTopicSelection )
        }
}