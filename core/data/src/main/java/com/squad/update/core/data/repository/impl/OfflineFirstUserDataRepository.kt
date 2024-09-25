package com.squad.update.core.data.repository.impl

import com.squad.update.core.data.repository.UserDataRepository
import com.squad.update.core.model.data.DarkThemeConfig
import com.squad.update.core.model.data.ThemeBrand
import com.squad.update.core.model.data.UserData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject

class OfflineFirstUserDataRepository @Inject constructor() : UserDataRepository {

    private val _userData = MutableSharedFlow<UserData>( replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST )
    private val currentUserData get() = _userData.replayCache.firstOrNull() ?: emptyUserData

    init {
        _userData.tryEmit( currentUserData )
    }

    override val userData: Flow<UserData> = _userData.filterNotNull()

    override suspend fun setFollowedTopicIds( followedTopicIds: Set<String> ) {
        _userData.tryEmit( currentUserData.copy( followedTopics = followedTopicIds ) )
    }

    override suspend fun setTopicIdFollowed( followedTopicId: String, followed: Boolean ) {
        currentUserData.let { current ->
            val followedTopics = if ( followed ) {
                current.followedTopics + followedTopicId
            } else {
                current.followedTopics - followedTopicId
            }
            _userData.tryEmit( current.copy( followedTopics = followedTopics ) )

        }
    }

    override suspend fun setNewsResourceBookmarked( newsResourceId: String, bookmarked: Boolean ) {
        currentUserData.let { current ->
            val bookmarkedNews = if ( bookmarked ) {
                current.bookmarkedNewsResources + newsResourceId
            } else {
                current.bookmarkedNewsResources - newsResourceId
            }
            _userData.tryEmit( current.copy( bookmarkedNewsResources = bookmarkedNews ) )
        }
    }

    override suspend fun setNewsResourceViewed( newsResourceId: String, viewed: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit(
                current.copy(
                    viewedNewsResources = if ( viewed ) {
                        current.viewedNewsResources + newsResourceId
                    } else {
                        current.viewedNewsResources - newsResourceId
                    }
                )
            )
        }
    }

    override suspend fun setThemeBrand( themeBrand: ThemeBrand ) {
        currentUserData.let { current ->
            _userData.tryEmit( current.copy( themeBrand = themeBrand ) )
        }
    }

    override suspend fun setDarkThemeConfig( darkThemeConfig: DarkThemeConfig ) {
        currentUserData.let { current ->
            _userData.tryEmit( current.copy( darkThemeConfig = darkThemeConfig ) )
        }
    }

    override suspend fun setDynamicColorPreference( useDynamicColor: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit( current.copy( useDynamicColor = useDynamicColor ) )
        }
    }

    override suspend fun setShouldHideOnboarding( shouldHideOnboarding: Boolean ) {
        currentUserData.let { current ->
            _userData.tryEmit( current.copy( shouldHideOnboarding = shouldHideOnboarding ) )
        }
    }
}

val emptyUserData = UserData(
    bookmarkedNewsResources = emptySet(),
    viewedNewsResources = emptySet(),
    followedTopics = emptySet(),
    themeBrand = ThemeBrand.DEFAULT,
    darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    useDynamicColor = true,
    shouldHideOnboarding = true
)