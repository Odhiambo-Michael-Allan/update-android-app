package com.squad.update.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.squad.update.R
import com.squad.update.core.designsystem.component.UpdateNavigationSuiteScaffold
import com.squad.update.navigation.TopLevelDestination

@Composable
fun UpdateApp(
    appState: UpdateAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    // If user is not connected to the internet show a snack bar to inform them.
    val notConnectedMessage = stringResource( id = R.string.not_connected )

    LaunchedEffect( key1 = isOffline ) {
        if ( isOffline ) {
            snackbarHostState.showSnackbar(
                message = notConnectedMessage,
                duration = SnackbarDuration.Indefinite
            )
        }
    }

    UpdateAppContent(
        appState = appState,
        snackbarHostState = snackbarHostState,
        windowAdaptiveInfo = windowAdaptiveInfo
    )
}

@Composable
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
internal fun UpdateAppContent(
    modifier: Modifier = Modifier,
    appState: UpdateAppState,
    snackbarHostState: SnackbarHostState,
    windowAdaptiveInfo: WindowAdaptiveInfo,
) {
    val unreadDestinations by appState.topLevelDestinationsWithUnreadResources
        .collectAsStateWithLifecycle()
    val currentDestination = appState.currentDestination

    UpdateNavigationSuiteScaffold(
        navigationSuiteItems = {
            appState.topLevelDestinations.forEach { destination ->
                val hasUnread = unreadDestinations.contains( destination )
                val selected = currentDestination
                    .isTopLevelDestinationInHierarchy( destination )
                item(
                    selected = selected,
                    onClick = { appState.navigateToTopLevelDestination( destination ) },
                    icon = {
                        Icon(
                            imageVector = destination.unselectedIcon,
                            contentDescription = null,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = destination.selectedIcon,
                            contentDescription = null
                        )
                    },
                    label = { Text(text = stringResource(id = destination.iconTextId))},
                    modifier = Modifier
                        .testTag( "UpdateNavItem" )
                        .then( if ( hasUnread ) Modifier.notificationDot() else Modifier )
                )
            }
        },
        windowAdaptiveInfo = windowAdaptiveInfo
    ) {

    }
}

private fun Modifier.notificationDot(): Modifier =
    composed {
        val tertiaryColor = MaterialTheme.colorScheme.tertiary
        drawWithContent {
            drawContent()
            drawCircle(
                tertiaryColor,
                radius = 5.dp.toPx(),
                // This is based on the dimensions of the NavigationBar's "indicator pill";
                // however, its parameters are private, so we must depend on them implicitly
                // ( NavigationBarTokens.ActiveIndicatorWidth = 64.dp )
                center = center + Offset(
                    64.dp.toPx() * .45f,
                    32.dp.toPx() * -.45f - 6.dp.toPx()
                )
            )
        }
    }

private fun NavDestination?.isTopLevelDestinationInHierarchy( destination: TopLevelDestination ) =
    this?.hierarchy?.any {
        it.route?.contains( destination.name, true ) ?: false
    } ?: false