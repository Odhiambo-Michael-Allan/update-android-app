package com.squad.update.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.squad.update.core.designsystem.theme.UpdateTheme

/**
 * Update filled button with generic content slot. Wraps Material 3 [Button].
 *
 * @param modifier Modifier to be applied to the button.
 * @param onClick Will be called when the user clicks the button.
 * @param enabled Controls the enabled state of the button. When 'false', this button will not be
 *        clickable and will appear disabled to accessibility services.
 * @param contentPadding The spacing values to apply internally between the container and the content.
 * @param content The button content.
 */
@Composable
fun UpdateButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    content: @Composable RowScope.() -> Unit,
) {
    FilledTonalButton(
        modifier = modifier,
        enabled = enabled,
        contentPadding = contentPadding,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = containerColor
        ),
        onClick = { /*TODO*/ }
    ) {
        content()
    }
}

/**
 * Update outlined button with generic content slot. Wraps Material 3 [OutlinedButton].
 *
 * @param modifier Modifier to be applied to the button.
 * @param onClick Will be called when the user clicks the button.
 * @param enabled Controls the enabled state of the button. When 'false', this button will not be
 *        clickable and will appear disabled to accessibility services.
 * @param contentPadding The spacing values to apply internally between the container and the content.
 * @param content The button content.
 */
@Composable
fun UpdateOutlinedButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        border = BorderStroke(
            width = UpdateButtonDefaults.OutlinedButtonBorderWidth,
            color = if ( enabled ) {
                MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.onSurface.copy(
                    alpha = UpdateButtonDefaults.DISABLED_OUTLINED_BUTTON_BORDER_ALPHA
                )
            }
        ),
        onClick = onClick
    ) {
        content()
    }
}

/**
 * Update button default values.
 */
object UpdateButtonDefaults {
    // TODO: File bug
    // OutlinedButton border color doesn't respect disabled state by default
    const val DISABLED_OUTLINED_BUTTON_BORDER_ALPHA = 0.12f

    // TODO: File bug
    // Outlined default border width isn't exposed via ButtonDefaults
    val OutlinedButtonBorderWidth = 1.dp
}

@ThemePreviews
@Composable
fun UpdateButtonPreview() {
    UpdateTheme {
        UpdateButton( onClick = { /*TODO*/ } ) {
            Text( text = "Test Button" )
        }
    }
}

@ThemePreviews
@Composable
fun UpdateOutlinedButtonPreview() {
    UpdateTheme {
        UpdateOutlinedButton(
            onClick = { /*TODO*/ }
        ) {
            Text( text = "Outlined Button" )
        }
    }
}