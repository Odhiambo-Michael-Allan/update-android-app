package com.squad.update.core.data.repository.impl

import com.squad.update.core.data.repository.UserDataRepository
import com.squad.update.core.datastore.UpdatePreferencesDataSource
import com.squad.update.core.model.data.DarkThemeConfig
import com.squad.update.core.model.data.ThemeBrand
import com.squad.update.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class OfflineFirstUserDataRepository @Inject constructor(
    private val updatePreferencesDataSource: UpdatePreferencesDataSource
) : UserDataRepository {

    override val userData: Flow<UserData> =
        updatePreferencesDataSource.userData

    override suspend fun setFollowedTopicIds( followedTopicIds: Set<String> ) {
        updatePreferencesDataSource.setFollowedTopicIds( followedTopicIds )
    }

    override suspend fun setTopicIdFollowed( followedTopicId: String, followed: Boolean ) {
        updatePreferencesDataSource.setTopicIdFollowed( followedTopicId, followed )
    }

    override suspend fun setNewsResourceBookmarked( newsResourceId: String, bookmarked: Boolean ) {
        updatePreferencesDataSource.setNewsResourceBookmarked( newsResourceId, bookmarked )
    }

    override suspend fun setNewsResourceViewed( newsResourceId: String, viewed: Boolean ) =
        updatePreferencesDataSource.setNewsResourceViewed( newsResourceId, viewed )

    override suspend fun setThemeBrand( themeBrand: ThemeBrand ) {
        updatePreferencesDataSource.setThemeBrand( themeBrand )
    }

    override suspend fun setDarkThemeConfig( darkThemeConfig: DarkThemeConfig ) {
        updatePreferencesDataSource.setDarkThemeConfig( darkThemeConfig )
    }

    override suspend fun setDynamicColorPreference( useDynamicColor: Boolean ) {
        updatePreferencesDataSource.setDynamicColorPreference( useDynamicColor )
    }

    override suspend fun setShouldHideTopicSelection(shouldHideTopicSelection: Boolean ) {
        updatePreferencesDataSource.setShouldHideTopicSelection( shouldHideTopicSelection )
    }

}