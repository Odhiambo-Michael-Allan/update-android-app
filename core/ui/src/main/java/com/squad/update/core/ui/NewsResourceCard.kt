package com.squad.update.core.ui

import java.time.format.FormatStyle
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.squad.update.core.model.data.UserNewsResource
import com.squad.update.core.designsystem.R.drawable
import com.squad.update.core.designsystem.icon.UpdateIcons
import com.squad.update.core.designsystem.theme.UpdateTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DefaultNewsResourceCard(
    modifier: Modifier = Modifier,
    userNewsResource: UserNewsResource,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape( 16.dp ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        onClick = onClick,
    ) {
        Column (
            modifier = Modifier.padding( 16.dp )
        ) {
            if ( !userNewsResource.headerImageUrl.isNullOrEmpty() ) {
                Row {
                    NewsResourceHeaderImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        userNewsResource.headerImageUrl
                    )
                }
            }
            Spacer( modifier = Modifier.height( 12.dp ) )
            NewsResourceTitle(
                modifier = Modifier.fillMaxWidth(),
                userNewsResource.title
            )
            if ( userNewsResource.headerImageUrl.isNullOrEmpty() ) {
                NewsResourceShortDescription( userNewsResource.content )
            }
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                NewsResourceMetadata(
                    modifier = Modifier.weight( 1f ),
                    publishDate = userNewsResource.publishDate
                )
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        imageVector = UpdateIcons.MoreVert,
                        contentDescription = null
                    )
                }
            }
            Spacer( modifier = Modifier.height( 14.dp ) )
        }
    }
}

@Composable
fun NewsResourceCardWithSideHeaderImage(
    modifier: Modifier = Modifier,
    userNewsResource: UserNewsResource,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape( 16.dp ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        onClick = onClick,
    ) {
        Column (
            modifier = Modifier.padding( 16.dp, 0.dp )
        ) {
            Spacer( modifier = Modifier.height( 12.dp ) )
            Row {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp),
                    text = userNewsResource.title,
                    style = MaterialTheme.typography.titleMedium
                )
                if ( !userNewsResource.headerImageUrl.isNullOrEmpty() ) {
                    NewsResourceHeaderImage(
                        modifier = Modifier.size( 120.dp ),
                        headerImageUrl = userNewsResource.headerImageUrl
                    )
                }
            }
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                NewsResourceMetadata(
                    modifier = Modifier.weight( 1f ),
                    publishDate = userNewsResource.publishDate
                )
                IconButton(
                    onClick = { /*TODO*/ }
                ) {
                    Icon(
                        imageVector = UpdateIcons.MoreVert,
                        contentDescription = null
                    )
                }
            }
            Spacer( modifier = Modifier.height( 14.dp ) )
        }
    }
}

@Composable
fun NewsResourceHeaderImage(
    modifier: Modifier,
    headerImageUrl: String?
) {
    var isLoading by remember { mutableStateOf( true ) }
    var isError by remember { mutableStateOf( false ) }
    val imageLoader = rememberAsyncImagePainter(
        model = headerImageUrl,
        onState = { state ->
            isLoading = state is AsyncImagePainter.State.Loading
            isError = state is AsyncImagePainter.State.Error
        }
    )
    val isLocalInspection = LocalInspectionMode.current

    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape( 12.dp ),
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            if ( isLoading ) {
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
                modifier = modifier,
                contentScale = ContentScale.Crop,
                painter = if ( isError.not() && !isLocalInspection ) {
                    imageLoader
                } else {
                    painterResource( drawable.core_designsystem_ic_placeholder_default )
                },
                contentDescription = null,
            )
        }
    }
}

@Composable
fun NewsResourceTitle(
    modifier: Modifier = Modifier,
    newsResourceTitle: String,
) {
    Text(
        modifier = modifier,
        text = newsResourceTitle,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun NewsResourceMetadata(
    modifier: Modifier = Modifier,
    publishDate: Instant,
) {
    val formattedDate = dateFormatted( publishDate )
    Text(
        modifier = modifier,
        text = formattedDate,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
fun dateFormatted( publishDate: Instant ): String = DateTimeFormatter
    .ofLocalizedDate( FormatStyle.MEDIUM )
    .withLocale( Locale.getDefault() )
    .withZone( LocalTimeZone.current.toJavaZoneId() )
    .format( publishDate.toJavaInstant() )

@Composable
fun NewsResourceShortDescription(
    newsResourceShortDescription: String,
) {
    Text(
        text = newsResourceShortDescription,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold,
        maxLines = 4,
        overflow = TextOverflow.Ellipsis,
    )
}

@Preview( "DefaultNewsResourceCard" )
@Composable
private fun DefaultNewsResourceCardPreview(
    @PreviewParameter( UserNewsResourcePreviewParameterProvider::class )
    userNewsResources: List<UserNewsResource>
) {
    CompositionLocalProvider(
        LocalInspectionMode provides true
    ) {
        UpdateTheme {
            Surface {
                DefaultNewsResourceCard(
                    userNewsResource = userNewsResources[2],
                    isBookmarked = true,
                    onToggleBookmark = {},
                    onClick = {}
                )
            }
        }
    }
}

@Preview( "NewsResourceCardWithSideHeaderImage" )
@Composable
private fun NewsResourceCardWithSideHeaderImagePreview(
    @PreviewParameter( UserNewsResourcePreviewParameterProvider::class )
    userNewsResources: List<UserNewsResource>
) {
    UpdateTheme {
        Surface {
            NewsResourceCardWithSideHeaderImage(
                userNewsResource = userNewsResources[2],
                isBookmarked = true,
                onToggleBookmark = {},
                onClick = {}
            )
        }
    }
}