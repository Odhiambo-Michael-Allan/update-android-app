package com.squad.update.core.designsystem.theme

import android.app.Activity
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val DarkColorScheme = ThemeColorSchemes.createDarkColorScheme(
    ThemeColors.PrimaryColor
)

private val LightColorScheme = ThemeColorSchemes.createLightColorScheme(
    ThemeColors.PrimaryColor
)

/**
 * Update Theme.
 *
 * @param darkTheme Whether the theme should use a dark color scheme ( follows system by default ).
 * @param androidTheme Whether the theme should use the Android theme color scheme instead of the
 *        default theme.
 * @param dynamicColor If 'true', disables the use of dynamic theming, even when it is
 *        supported. This parameter has no effect if [androidTheme] is 'true'.
 */
@Composable
fun UpdateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

//    val colorScheme = when {
//        !disableDynamicTheming && supportsDynamicTheming() -> {
//            val context = LocalContext.current
//            if ( darkTheme ) dynamicDarkColorScheme( context )
//            else dynamicLightColorScheme( context )
//        }
//        else -> {
//            if ( darkTheme ) ThemeColorSchemes.createDarkColorScheme( ThemeColors.PrimaryColor )
//            else ThemeColorSchemes.createLightColorScheme( ThemeColors.PrimaryColor )
//        }
//    }
//
//    val view = LocalView.current
//    if ( !view.isInEditMode ) {
//        SideEffect {
//            val window = ( view.context as Activity ).window
//            window.navigationBarColor = colorScheme.surfaceColorAtElevation(
//                NavigationBarDefaults.Elevation
//            ).toArgb()
//        }
//    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if ( !view.isInEditMode ) {
        SideEffect {
            val window = ( view.context as Activity ).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surfaceContainer.toArgb()
        }
    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = UpdateTypography,
        content = content
    )
}

@ChecksSdkIntAtLeast( api = Build.VERSION_CODES.S )
fun supportsDynamicTheming() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S