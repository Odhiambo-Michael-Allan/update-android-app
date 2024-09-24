package com.squad.update.core.designsystem.component

import android.R
import androidx.annotation.StringRes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.squad.update.core.designsystem.icon.UpdateIcons
import com.squad.update.core.designsystem.theme.UpdateTheme

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun UpdateTopAppBar(
    modifier: Modifier = Modifier,
    @StringRes titleRes: Int,
    navigationIcon: ImageVector,
    navigationIconContentDescription: String,
    actionIcon: ImageVector,
    actionIconContentDescription: String,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        modifier = modifier.testTag( "updateTopAppBar" ),
        title = {
            Text( text = stringResource( id = titleRes ) )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigationClick
            ) {
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = navigationIconContentDescription,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            IconButton(
                onClick = onActionClick
            ) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = actionIconContentDescription,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = colors,
    )
}

@OptIn( ExperimentalMaterial3Api::class )
@Preview( "Top App Bar" )
@Composable
private fun UpdateTopAppBarPreview() {
    UpdateTheme {
        UpdateTopAppBar(
            titleRes = R.string.untitled,
            navigationIcon = UpdateIcons.Search,
            navigationIconContentDescription = "Navigation icon",
            actionIcon = UpdateIcons.MoreVert,
            actionIconContentDescription = "Action icon"
        )
    }
}