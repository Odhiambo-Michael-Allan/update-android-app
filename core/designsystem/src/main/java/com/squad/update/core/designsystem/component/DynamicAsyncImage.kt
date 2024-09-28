package com.squad.update.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.squad.update.core.designsystem.R
import com.squad.update.core.designsystem.theme.LocalTintTheme

/**
 * A wrapper around [AsyncImage] which determines the colorFilter based on the theme.
 */
@Composable
fun DynamicAsyncImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    contentDescription: String?,
    placeholder: Painter = painterResource(
        id = R.drawable.core_designsystem_ic_placeholder_default
    )
) {
    val iconTint = LocalTintTheme.current.iconTint
    var isLoading by remember { mutableStateOf( true ) }
    var isError by remember { mutableStateOf( false ) }
    val imageLoader = rememberAsyncImagePainter(
        model = imageUrl,
        onState = { state ->
            isLoading = state is AsyncImagePainter.State.Loading
            isError = state is AsyncImagePainter.State.Error
        }
    )
    val isLocalInspection = LocalInspectionMode.current
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if ( isLoading && !isLocalInspection ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                        alpha = .7f
                    )
                )
            ) {}
        }
        Image(
            contentScale = ContentScale.Crop,
            painter = if ( isError.not() && !isLocalInspection ) imageLoader else placeholder,
            contentDescription = contentDescription,
            colorFilter = if ( iconTint != Unspecified ) ColorFilter.tint( iconTint ) else null
        )
    }
}