package com.squad.update.feature.foryou.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

const val LINKED_NEWS_RESOURCE_ID = "linkedNewsResourceId"
const val FOR_YOU_ROUTE = "for_you_route/{$LINKED_NEWS_RESOURCE_ID}"
private const val DEEP_LINK_URI_PATTERN =
    "https://www.update.squad.com/foryou/{$LINKED_NEWS_RESOURCE_ID}"

fun NavController.navigateToForYou( navOptions: NavOptions ) = navigate( FOR_YOU_ROUTE, navOptions )