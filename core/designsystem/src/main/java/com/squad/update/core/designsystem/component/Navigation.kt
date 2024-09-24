package com.squad.update.core.designsystem.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.squad.update.core.designsystem.icon.UpdateIcons
import com.squad.update.core.designsystem.theme.UpdateTheme

/**
 * Update navigation suite scaffold with item and content slots. Wraps Material 3
 * [NavigationSuiteScaffold].
 *
 * @param modifier Modifier to be applied to the navigation suite scaffold.
 * @param navigationSuiteItems A slot to display multiple items via [UpdateNavigationSuiteScope].
 * @param windowAdaptiveInfo The window adaptive info.
 * @param content The app content inside the scaffold.
 */
@Composable
fun UpdateNavigationSuiteScaffold(
    modifier: Modifier = Modifier,
    navigationSuiteItems: UpdateNavigationSuiteScope.() -> Unit,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    content: @Composable () -> Unit,
) {
    val layoutType = NavigationSuiteScaffoldDefaults
        .calculateFromAdaptiveInfo( windowAdaptiveInfo )
    val navigationSuiteItemColors = NavigationSuiteItemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = UpdateNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = UpdateNavigationDefaults.navigationContentColor(),
            selectedTextColor = UpdateNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = UpdateNavigationDefaults.navigationContentColor(),
            indicatorColor = UpdateNavigationDefaults.navigationIndicatorColor()
        ),
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            selectedIconColor = UpdateNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = UpdateNavigationDefaults.navigationContentColor(),
            selectedTextColor = UpdateNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = UpdateNavigationDefaults.navigationContentColor(),
            indicatorColor = UpdateNavigationDefaults.navigationIndicatorColor()
        ),
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            selectedIconColor = UpdateNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = UpdateNavigationDefaults.navigationContentColor(),
            selectedTextColor = UpdateNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = UpdateNavigationDefaults.navigationContentColor()
        )
    )

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            UpdateNavigationSuiteScope(
                navigationSuiteScope = this,
                navigationSuiteItemColors = navigationSuiteItemColors
            ).run( navigationSuiteItems )
        },
        layoutType = layoutType,
        containerColor = Color.Transparent,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContentColor = UpdateNavigationDefaults.navigationContentColor(),
            navigationRailContainerColor = Color.Transparent
        ),
        modifier = modifier,
    ) {
        content()
    }
}

/**
 * A wrapper around [NavigationSuiteScope] to declare navigation items.
 */
class UpdateNavigationSuiteScope internal constructor(
    private val navigationSuiteScope: NavigationSuiteScope,
    private val navigationSuiteItemColors: NavigationSuiteItemColors
) {
    fun item(
        modifier: Modifier,
        selected: Boolean,
        onClick: () -> Unit,
        icon: @Composable () -> Unit,
        selectedIcon: @Composable () -> Unit = icon,
        label: @Composable ( () -> Unit )? = null,
    ) = navigationSuiteScope.item(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        icon = {
            if ( selected ) selectedIcon()
            else icon()
        },
        label = label,
        colors = navigationSuiteItemColors,
    )
}

/**
 * Update navigation rail with header and content slots. Wraps Material 3 [Navigation Rail].
 *
 * @param modifier Modifier to be applied to the navigation rail.
 * @param header Optional header that may hold a floating action button or a logo.
 * @param content Destinations inside the navigation rail. This should contain multiple
 * [NavitationRailItem]s
 */
@Composable
fun UpdateNavigationRail(
    modifier: Modifier = Modifier,
    header: @Composable ( ColumnScope.() -> Unit )? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    NavigationRail(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = UpdateNavigationDefaults.navigationContentColor(),
        header = header,
        content = content
    )
}

/**
 * Update navigation rail item with icon and label content slots. Wraps Material 3
 * [NavigationRailItem]
 *
 * @param modifier Modifier to be applied to this item.
 * @param selected Whether this item is selected.
 * @param onClick The callback to be invoked when this item is selected.
 * @param icon The item icon content.
 * @param selectedIcon The item icon content when selected.
 * @param enabled Controls the enabled state of this item. When 'false', this item will not be
 * clickable and will appear disabled to accessibility services.
 * @param label The item text label content
 * @param alwaysShowLabel Whether to always show the label for this item. If false, this label will
 * only be shown when this item is selected.
 */
@Composable
fun UpdateNavigationRailItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable ( () -> Unit )? = null
) {
    NavigationRailItem(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        icon = if ( selected ) selectedIcon else icon,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = UpdateNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = UpdateNavigationDefaults.navigationContentColor(),
            selectedTextColor = UpdateNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = UpdateNavigationDefaults.navigationContentColor(),
            indicatorColor = UpdateNavigationDefaults.navigationIndicatorColor()
        )
    )
}

/**
 * Update navigation bar with content slots. Wraps Material 3 [NavigationBar].
 *
 * @param modifier Modifier to be applied to the navigation bar.
 * @param content Destinations inside the navigation bar. This should contain multiple
 * [NavigationBarItem]s.
 */
@Composable
fun UpdateNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        contentColor = UpdateNavigationDefaults.navigationContentColor(),
        tonalElevation = 0.dp,
        content = content
    )
}

/**
 * Now in Android navigation bar item with icon and label content slots. Wraps Material 3
 * [NavigationBarItem].
 *
 * @param selected Whether this item is selected.
 * @param onClick The callback to be invoked when this item is selected.
 * @param icon The item icon content.
 * @param modifier Modifier to be applied to this item.
 * @param selectedIcon The item icon content when selected.
 * @param enabled controls the enabled state of this item. When `false`, this item will not be
 * clickable and will appear disabled to accessibility services.
 * @param label The item text label content.
 * @param alwaysShowLabel Whether to always show the label for this item. If false, the label will
 * only be shown when this item is selected.
 */
@Composable
fun RowScope.UpdateNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = UpdateNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = UpdateNavigationDefaults.navigationContentColor(),
            selectedTextColor = UpdateNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = UpdateNavigationDefaults.navigationContentColor(),
            indicatorColor = UpdateNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

/**
 * Update navigation default values.
 */
object UpdateNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}

@ThemePreviews
@Composable
fun UpdateNavigationRailPreview() {
    val items = listOf( "For you", "Saved", "Interests" )
    val icons = listOf(
        UpdateIcons.UpcomingBorder,
        UpdateIcons.BookmarksBorder,
        UpdateIcons.Grid3x3
    )
    val selectedIcons = listOf(
        UpdateIcons.Upcoming,
        UpdateIcons.Bookmarks,
        UpdateIcons.Grid3x3
    )
    UpdateTheme {
        UpdateNavigationRail {
            items.forEachIndexed { index, item ->
                UpdateNavigationRailItem(
                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = item
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = selectedIcons[index],
                            contentDescription = item,
                        )
                    },
                    label = { Text( text = item ) },
                    selected = index == 0,
                    onClick = {}
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun UpdateNavigationBarPreview() {
    val items = listOf( "For you", "Saved", "Interests" )
    val icons = listOf(
        UpdateIcons.UpcomingBorder,
        UpdateIcons.BookmarkBorder,
        UpdateIcons.Grid3x3
    )
    val selectedIcons = listOf(
        UpdateIcons.Upcoming,
        UpdateIcons.Bookmarks,
        UpdateIcons.Grid3x3
    )

    UpdateTheme {
        UpdateNavigationBar {
            items.forEachIndexed { index, item ->
                UpdateNavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = item
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = selectedIcons[index],
                            contentDescription = item,
                        )
                    },
                    label = { Text( text = item ) },
                    selected = index == 0,
                    onClick = {}
                )
            }
        }
    }
}