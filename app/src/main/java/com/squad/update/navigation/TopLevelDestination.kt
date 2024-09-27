package com.squad.update.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.squad.update.R
import com.squad.update.core.designsystem.icon.UpdateIcons
import com.squad.update.feature.foryou.R as forYouR
import com.squad.update.feature.following.R as followingR

/**
 * Type for the top level destination in the application. Each of these destinations can contain
 * on or more screens ( based on the window size ). Navigation from one screen to the next
 * within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination (
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int,
    val titleTextId: Int,
) {
    FOR_YOU(
        selectedIcon = UpdateIcons.ForYou,
        unselectedIcon = UpdateIcons.ForYouBorder,
        iconTextId = forYouR.string.feature_foryou_title,
        titleTextId = R.string.app_name
    ),
    FOLLOWING(
        selectedIcon = UpdateIcons.Following,
        unselectedIcon = UpdateIcons.FollowingBorder,
        iconTextId = followingR.string.feature_following_title,
        titleTextId = followingR.string.feature_following_title,

    )
}