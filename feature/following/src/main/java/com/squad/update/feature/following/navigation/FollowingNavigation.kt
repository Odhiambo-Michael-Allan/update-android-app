package com.squad.update.feature.following.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions

import androidx.navigation.compose.composable
import com.squad.update.feature.following.FollowingScreen
import kotlinx.serialization.Serializable

@Serializable
data object FollowingRoute

fun NavController.navigateToFollowing( navOptions: NavOptions ) =
    navigate( route = FollowingRoute, navOptions )

fun NavGraphBuilder.followingScreen() {
    composable<FollowingRoute> {
        FollowingScreen {}
    }
}