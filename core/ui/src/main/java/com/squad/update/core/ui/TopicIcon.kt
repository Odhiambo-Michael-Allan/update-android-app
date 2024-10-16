package com.squad.update.core.ui

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.squad.update.core.designsystem.component.DynamicAsyncImage

@Composable
fun TopicIcon(
    modifier: Modifier = Modifier,
    imageUrl: String,
) {
    DynamicAsyncImage(
        modifier = modifier
            .size( 18.dp ),
        placeholder = painterResource( id = R.drawable.ic_icon_placeholder ),
        imageUrl = imageUrl,
        contentDescription = null,
    )
}