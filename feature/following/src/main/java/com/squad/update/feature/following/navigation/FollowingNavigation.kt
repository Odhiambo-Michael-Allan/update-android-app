package com.squad.update.feature.following.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions

import androidx.navigation.compose.composable
import com.squad.update.feature.following.FollowingScreen
import kotlinx.serialization.Serializable

@Serializable
data object ForYouRoute

fun NavController.navigateToFollowing( navOptions: NavOptions ) =
    navigate( route = ForYouRoute, navOptions )

fun NavGraphBuilder.followingScreen() {
    composable<ForYouRoute> {
        FollowingScreen {}
    }
}