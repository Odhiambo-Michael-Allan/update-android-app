package com.squad.update.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.squad.update.core.data.repository.UserNewsResourceRepository
import com.squad.update.core.data.util.NetworkMonitor
import com.squad.update.core.data.util.TimeZoneMonitor
import com.squad.update.feature.following.navigation.navigateToFollowing
import com.squad.update.feature.foryou.navigation.FOR_YOU_ROUTE
import com.squad.update.feature.foryou.navigation.navigateToForYou
import com.squad.update.navigation.TopLevelDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone

@Composable
fun rememberUpdateAppState(
    networkMonitor: NetworkMonitor,
    userNewsResourceRepository: UserNewsResourceRepository,
    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController()
) : UpdateAppState {
    return remember(
        navController,
        coroutineScope,
        networkMonitor,
        userNewsResourceRepository,
        timeZoneMonitor,
    ) {
        UpdateAppState(
            navController = navController,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor,
            userNewsResourceRepository = userNewsResourceRepository,
            timeZoneMonitor = timeZoneMonitor,
        )
    }
}

@Stable
class UpdateAppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    userNewsResourceRepository: UserNewsResourceRepository,
    timeZoneMonitor: TimeZoneMonitor
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when ( currentDestination?.route ) {
            FOR_YOU_ROUTE -> TopLevelDestination.FOR_YOU
            else -> null
        }

    val isOffline = networkMonitor.isOnline
        .map( Boolean::not )
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = false,
        )

    /**
     * Map of top level destinations to be used in the TopBar, BottomBar and NavRail. The key is the
     * route.
     */
    val topLevelDestinations: List<TopLevelDestination> = TopLevelDestination.entries

    /**
     * The top level destinations that have unread news resources
     */
    val topLevelDestinationsWithUnreadResources: StateFlow<Set<TopLevelDestination>> =
        userNewsResourceRepository.observeAllForFollowedTopics()
            .combine( userNewsResourceRepository.observeAllBookmarked() ) { forYouNewsResources, bookmarkedNewsResources ->
                setOfNotNull(
                    TopLevelDestination.FOR_YOU.takeIf { forYouNewsResources.any { !it.hasBeenViewed } },
                )

            }.stateIn(
                coroutineScope,
                SharingStarted.WhileSubscribed( 5_000 ),
                initialValue = emptySet()
            )

    val currentTimeZone = timeZoneMonitor.currentTimeZone
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed( 5_000 ),
            TimeZone.currentSystemDefault()
        )

    /**
     * UI logic for navigating to a top level destination in the app. Top level destinations have
     * only one copy of the destination of the back stack, and save and restore state whenever you
     * navigate to and from it.
     *
     * @param topLevelDestination: The destination the app needs to navigate to.
     */
    fun navigateToTopLevelDestination( topLevelDestination: TopLevelDestination ) {
        val topLevelNavOptions = navOptions {
            // Pop up to the start destination of the graph to avoid building
            // up a large stack of destinations on the back stack as users
            // select items
            popUpTo( navController.graph.findStartDestination().id ) {
                saveState = true
            }
            // Avoid multiple copies of the same destination when re-selecting the same item
            launchSingleTop = true
            // Restore state when re-selecting a previously selected item.
            restoreState = true
        }
        when ( topLevelDestination ) {
            TopLevelDestination.FOR_YOU -> navController.navigateToForYou( topLevelNavOptions )
            TopLevelDestination.FOLLOWING -> navController.navigateToFollowing( topLevelNavOptions )
        }
    }

}