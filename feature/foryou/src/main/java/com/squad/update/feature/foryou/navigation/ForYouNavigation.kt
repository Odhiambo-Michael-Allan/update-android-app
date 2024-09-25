package com.squad.update.feature.foryou.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.squad.update.feature.foryou.ForYouRoute

const val LINKED_NEWS_RESOURCE_ID = "linkedNewsResourceId"
const val FOR_YOU_ROUTE = "for_you_route/{$LINKED_NEWS_RESOURCE_ID}"
private const val DEEP_LINK_URI_PATTERN =
    "https://www.update.squad.com/foryou/{$LINKED_NEWS_RESOURCE_ID}"

fun NavController.navigateToForYou( navOptions: NavOptions ) = navigate( FOR_YOU_ROUTE, navOptions )

fun NavGraphBuilder.forYouScreen( onTopicClick: ( String ) -> Unit ) {
    composable(
        route = FOR_YOU_ROUTE,
        arguments = listOf(
            navArgument( LINKED_NEWS_RESOURCE_ID ) { type = NavType.StringType }
        )
    ) {
        ForYouRoute(
            onTopicClick = onTopicClick
        )
    }
}