package com.squad.update.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.squad.update.core.model.data.DarkThemeConfig
import com.squad.update.core.model.data.ThemeBrand
import com.squad.update.core.model.data.UserData
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UpdatePreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    val userData = userPreferences.data
        .map {
            UserData(
                bookmarkedNewsResources = it.bookmarkedNewsResourceIdsMap.keys,
                viewedNewsResources = it.viewedNewsResourceIdsMap.keys,
                followedTopics = it.followedTopicIdsMap.keys,
                themeBrand = when ( it.themeBrand ) {
                    null,
                    ThemeBrandProto.THEME_BRAND_UNSPECIFIED,
                    ThemeBrandProto.UNRECOGNIZED,
                    ThemeBrandProto.THEME_BRAND_DEFAULT
                    -> ThemeBrand.DEFAULT
                    ThemeBrandProto.THEME_BRAND_ANDROID -> ThemeBrand.ANDROID
                },
                darkThemeConfig = when ( it.darkThemeConfig ) {
                    null,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.UNRECOGNIZED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    -> DarkThemeConfig.FOLLOW_SYSTEM
                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT ->
                        DarkThemeConfig.LIGHT
                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
                },
                useDynamicColor = it.useDynamicColor,
                shouldHideTopicSelection = it.shouldHideTopicSelection
            )
        }

    suspend fun setShouldHideTopicSelection( shouldHideTopicSelection: Boolean ) {
        userPreferences.updateData {
            it.copy { this.shouldHideTopicSelection = shouldHideTopicSelection }
        }
    }

    suspend fun setTopicIdFollowed( topicId: String, followed: Boolean ) {
        try {
            userPreferences.updateData {
                it.copy {
                    if ( followed ) followedTopicIds.put( topicId, true )
                    else followedTopicIds.remove( topicId )
                    updateShouldHideTopicSelectionIfNecessary()
                }
            }
        } catch ( ioException: IOException ) {
            Log.e( "UPDATE PREFERENCES", "Failed to update user preferences", ioException )
        }
    }

    suspend fun setFollowedTopicIds( topicIds: Set<String> ) {
        try {
            userPreferences.updateData {
                it.copy {
                    followedTopicIds.clear()
                    followedTopicIds.putAll( topicIds.associateWith { true } )
                    updateShouldHideTopicSelectionIfNecessary()
                }
            }
        } catch ( ioException: IOException ) {
            Log.e(
                "UPDATE-PREFERENCES-DATA-SOURCE",
                "Failed to update user preferences",
                ioException
            )
        }
    }

    suspend fun setDynamicColorPreference( useDynamicColor: Boolean ) {
        userPreferences.updateData {
            it.copy { this.useDynamicColor = useDynamicColor }
        }
    }

    suspend fun setDarkThemeConfig( darkThemeConfig: DarkThemeConfig ) {
        userPreferences.updateData {
            it.copy {
                this.darkThemeConfig = when ( darkThemeConfig ) {
                    DarkThemeConfig.FOLLOW_SYSTEM ->
                        DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }

    suspend fun setNewsResourceBookmarked( newsResourceId: String, bookmarked: Boolean ) {
        try {
            userPreferences.updateData {
                it.copy {
                    if ( bookmarked ) bookmarkedNewsResourceIds.put( newsResourceId, true )
                    else bookmarkedNewsResourceIds.remove( newsResourceId )
                }
            }
        } catch ( ioException: IOException ) {
            Log.e(
                "UPDATE-PREFERENCES-DATA-SOURCE",
                "Failed to update user preferences",
                ioException
            )
        }
    }

    suspend fun setNewsResourceViewed( newsResourceId: String, viewed: Boolean ) {
        setNewsResourcesViewed( listOf( newsResourceId ), viewed )
    }

    suspend fun setNewsResourcesViewed( newsResourceIds: List<String>, viewed: Boolean ) {
        userPreferences.updateData { prefs ->
            prefs.copy {
                newsResourceIds.forEach { id ->
                    if ( viewed ) viewedNewsResourceIds.put( id, true )
                    else viewedNewsResourceIds.remove( id )
                }
            }
        }
    }

    suspend fun setThemeBrand( themeBrand: ThemeBrand ) {
        userPreferences.updateData {
            it.copy {
                this.themeBrand = when ( themeBrand ) {
                    ThemeBrand.DEFAULT -> ThemeBrandProto.THEME_BRAND_DEFAULT
                    ThemeBrand.ANDROID -> ThemeBrandProto.THEME_BRAND_ANDROID
                }
            }
        }
    }
}

private fun UserPreferencesKt.Dsl.updateShouldHideTopicSelectionIfNecessary() {
    if ( followedTopicIds.isEmpty() && followedAuthorIds.isEmpty() ) {
        shouldHideTopicSelection = false
    }
}