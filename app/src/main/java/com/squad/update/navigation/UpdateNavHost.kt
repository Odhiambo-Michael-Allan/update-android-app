package com.squad.update.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.squad.update.feature.foryou.navigation.FOR_YOU_ROUTE
import com.squad.update.ui.UpdateAppState

/**
 * Top-level navigation graph. Navigation is organized as explained at
 * https://d.android.com/jetpack/compose/nav-adaptive
 *
 * The navigation graph defined in this file defines the different top level routes. Navigation
 * within each route is handled using state and Back Handlers.
 */
@Composable
fun UpdateNavHost(
    modifier: Modifier = Modifier,
    appState: UpdateAppState,
    onShowSnackBar: suspend ( String, String? ) -> Boolean,
    startDestination: String = FOR_YOU_ROUTE,
) {
    val navController = appState.navController
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        forYouScreen( onTopicClick = {} )
    }
}