package com.squad.update.feature.following.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.update.feature.following.FollowingRoute

const val FOLLOWING_ROUTE = "following_route"

fun NavController.navigateToFollowing( navOptions: NavOptions ) =
    navigate( FOLLOWING_ROUTE, navOptions )

fun NavGraphBuilder.followingScreen() {
    composable(
        route = FOLLOWING_ROUTE,
    ) {
        FollowingRoute()
    }
}