package com.squad.update.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.squad.update.R
import com.squad.update.core.designsystem.component.UpdateNavigationSuiteScaffold
import com.squad.update.core.designsystem.component.UpdateTopAppBar
import com.squad.update.core.designsystem.icon.UpdateIcons
import com.squad.update.navigation.TopLevelDestination
import com.squad.update.navigation.UpdateNavHost

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
        modifier = modifier,
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
                    label = {
                        Text(
                            text = stringResource( id = destination.iconTextId ),
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    modifier = Modifier
                        .testTag("UpdateNavItem")
                        .then(if (hasUnread) Modifier.notificationDot() else Modifier)
                )
            }
        },
        windowAdaptiveInfo = windowAdaptiveInfo
    ) {
        Scaffold (
            modifier = modifier.semantics {
                testTagsAsResourceId = true
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets( 0, 0, 0, 0 ),
            snackbarHost = { SnackbarHost( hostState = snackbarHostState ) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal
                        )
                    )
            ) {
                // Show the top app bar on top level destinations.
//                val destination = appState.currentTopLevelDestination
                val destination = TopLevelDestination.FOR_YOU
                val shouldShowTopAppBar = destination != null
                if ( destination != null ) {
                    UpdateTopAppBar(
                        titleRes = destination.titleTextId,
                        navigationIcon = UpdateIcons.Search,
                        navigationIconContentDescription = "",
                        actionIcon = UpdateIcons.Settings,
                        actionIconContentDescription = "",
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent
                        ),
                        onActionClick = {},
                        onNavigationClick = {}
                    )
                }
                Box(
                    // Workaround for https://issuetracker.google.com/338478720
                    modifier = Modifier.consumeWindowInsets(
                        if ( shouldShowTopAppBar ) WindowInsets.safeDrawing.only( WindowInsetsSides.Top )
                        else WindowInsets( 0, 0, 0, 0 )
                    )
                ) {
                    UpdateNavHost(
                        appState = appState,
                        onShowSnackBar = { message, action ->
                            snackbarHostState.showSnackbar(
                                message = message,
                                actionLabel = action,
                                duration = SnackbarDuration.Short
                            ) == SnackbarResult.ActionPerformed
                        }
                    )
                }
            }
        }
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