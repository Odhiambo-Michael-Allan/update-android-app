package com.squad.update

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.squad.update.MainActivityUiState.*
import com.squad.update.core.data.repository.UserNewsResourceRepository
import com.squad.update.core.data.util.NetworkMonitor
import com.squad.update.core.data.util.TimeZoneMonitor
import com.squad.update.core.designsystem.theme.ThemeColorSchemes
import com.squad.update.core.model.data.DarkThemeConfig
import com.squad.update.core.model.data.ThemeBrand
import com.squad.update.core.ui.LocalTimeZone
import com.squad.update.ui.rememberUpdateAppState
import com.squad.update.core.designsystem.theme.UpdateTheme
import com.squad.update.ui.UpdateApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    @Inject
    lateinit var userNewsResourceRepository: UserNewsResourceRepository

    val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate( savedInstanceState: Bundle? ) {
        val splashScreen = installSplashScreen()
        super.onCreate( savedInstanceState )

        var uiState: MainActivityUiState by mutableStateOf( Loading )

        // Update the uiState
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle( Lifecycle.State.STARTED ) {
                viewModel.uiState
                    .onEach { uiState = it }
                    .collect()
            }
        }

        // Keep the splash screen on-screen until the UI state is loaded. This condition is
        // evaluated each time the app needs to be redrawn so it should be fast to avoid
        // blocking the UI.
        splashScreen.setKeepOnScreenCondition {
            when ( uiState ) {
                Loading -> true
                is Success -> false
            }
        }

        // Turn off the decor fitting system windows, which allows us to handle insets, including
        // IME animations, and go edge-to-edge. This also sets up the initial system bar style
        // based on the platform theme.
        enableEdgeToEdge()

        setContent {

            val darkTheme = shouldUseDarkTheme( uiState )

            // Update the edge to edge configuration to match the theme. This is the same parameters
            // as the default enableEdgeToEdge call, but we manually resolve whether or not to show
            // dark theme using uiState, since it can be different than the configuration's dark
            // theme value based on the user preference.
//            DisposableEffect( darkTheme ) {
//                enableEdgeToEdge(
//                    statusBarStyle = SystemBarStyle.auto(
//                        lightScrim = lightScrim,
//                        darkScrim = darkScrim
//                    ) { darkTheme },
//                    navigationBarStyle = SystemBarStyle.auto(
//                        lightScrim = ThemeColorSchemes.lightScrim.toArgb(),
//                        darkScrim = ThemeColorSchemes.darkScrim.toArgb()
//                    ) { darkTheme },
//                )
//                onDispose {}
//            }

            val appState = rememberUpdateAppState(
                networkMonitor = networkMonitor,
                userNewsResourceRepository = userNewsResourceRepository,
                timeZoneMonitor = timeZoneMonitor,
            )

            val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()

            CompositionLocalProvider(
                LocalTimeZone provides currentTimeZone
            ) {
                UpdateTheme(
                    darkTheme = darkTheme,
                    dynamicColor = false
//                    dynamicColor = shouldDisableDynamicTheming( uiState = uiState ),
                ) {
                    UpdateApp( appState )
                }
            }
        }
    }

}

/**
 * Returns 'true' if the Android theme should be used, as a function of the [uiState]
 */
@Composable
private fun shouldUseAndroidTheme(
    uiState: MainActivityUiState
): Boolean = when ( uiState ) {
    Loading -> false
    is Success -> when ( uiState.userData.themeBrand ) {
        ThemeBrand.DEFAULT -> false
        ThemeBrand.ANDROID -> true
    }
}

/**
 * Returns 'true' if the dynamic color is disabled, as a function of the [uiState]
 */
@Composable
private fun shouldDisableDynamicTheming(
    uiState: MainActivityUiState
): Boolean = when ( uiState ) {
    Loading -> false
    is Success -> !uiState.userData.useDynamicColor
}

/**
 * Returns 'true' if dark theme should be used, as a function of the [uiState] and the current
 * system context.
 */
@Composable fun shouldUseDarkTheme(
    uiState: MainActivityUiState
): Boolean = when ( uiState ) {
    Loading -> isSystemInDarkTheme()
    is Success -> when ( uiState.userData.darkThemeConfig ) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }
}

/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = android.graphics.Color.argb( 0xe6, 0xFF, 0xFF, 0xFF )

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = android.graphics.Color.argb( 0x80, 0x1b, 0x1b, 0x1b )