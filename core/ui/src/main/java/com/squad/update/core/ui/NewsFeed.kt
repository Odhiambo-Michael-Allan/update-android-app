package com.squad.update.core.ui

import android.content.Context
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.squad.update.core.designsystem.component.UpdateButton
import com.squad.update.core.designsystem.icon.UpdateIcons
import com.squad.update.core.designsystem.theme.UpdateTheme
import com.squad.update.core.model.data.FollowableTopic
import com.squad.update.core.model.data.UserNewsResource
import kotlin.math.min

/**
 * An extension on [LazyListScope] defining a feed with news resources. Depending on the [feedState],
 * this might emit no items.
 */
fun LazyGridScope.newsFeed(
    feedState: NewsFeedUiState,
) {
    when ( feedState ) {
        NewsFeedUiState.Loading -> Unit
        is NewsFeedUiState.Success -> {
            feedState.userNewsResources
                .filter{ newsResource ->
                    newsResource.followableTopics.first().isFollowed
                }.groupBy {
                    it.followableTopics.first()
                }.forEach { ( followableTopic, newsResources ) ->
                    item (
                        span = {
                            GridItemSpan( maxLineSpan )
                        }
                    ) {
                        TopicHeader(
                            modifier = Modifier.padding( 16.dp, 4.dp ),
                            followableTopic = followableTopic,
                            onToggleFollowed = {}
                        )
                    }
                    newsResources.subListNonStrict( 3 ).forEach { newsResource ->
                        item {
                            NewsResourceCardWithSideHeaderImage(
                                userNewsResource = newsResource,
                                isBookmarked = newsResource.isSaved,
                                onToggleBookmark = { /*TODO*/ },
                                onClick = {}
                            )
                        }
                    }
                    item (
                        span = {
                            GridItemSpan( maxLineSpan )
                        },
                    ) {
                        UpdateButton(
                            modifier = Modifier.padding( 16.dp ),
                            onClick = { /*TODO*/ }
                        ) {
                            Text(
                                text = stringResource(
                                    id = R.string.more_stories_about_topic,
                                    followableTopic.topic.name
                                ),
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    item(
                        span = {
                            GridItemSpan( maxLineSpan )
                        },
                    ) {
                        HorizontalDivider(
                            thickness = 10.dp,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
            }
        }
    }
}

@Composable
fun TopicHeader(
    modifier: Modifier = Modifier,
    followableTopic: FollowableTopic,
    onToggleFollowed: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row (
            modifier = Modifier.weight( 1f ),
            horizontalArrangement = Arrangement.spacedBy( 12.dp ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopicIcon(
                imageUrl = followableTopic.topic.imageUrl
            )
            Text(
                text = followableTopic.topic.name,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        IconButton(
            onClick = onToggleFollowed
        ) {
            Icon(
                imageVector = if ( followableTopic.isFollowed ) {
                    UpdateIcons.Following
                } else {
                    UpdateIcons.FollowingBorder
                },
                contentDescription = null
            )
        }
    }
}


fun launchCustomChromeTab( context: Context, uri: Uri, @ColorInt toolbarColor: Int ) {
    val customTabBarColor = CustomTabColorSchemeParams.Builder()
        .setToolbarColor( toolbarColor ).build()
    val customTabsIntent = CustomTabsIntent.Builder()
        .setDefaultColorSchemeParams( customTabBarColor )
        .build()
    customTabsIntent.launchUrl( context, uri )
}

/**
 * A sealed hierarchy describing the state of the feed of news resources
 */
sealed interface NewsFeedUiState {
    /**
     * The feed is still loading.
     */
    data object Loading : NewsFeedUiState

    /**
     * The feed is loaded with the given list of news resources
     */
    data class Success(
        /**
         * The list of news resources contained in this feed.
         */
        val userNewsResources: List<UserNewsResource>
    ) : NewsFeedUiState
}

fun <T> List<T>.subListNonStrict( length: Int, start: Int = 0 ) =
    subList( start, min( start + length, size ))

@Preview
@Composable
private fun NewsFeedLoadingPreview() {
    UpdateTheme {
        LazyVerticalGrid( columns = GridCells.Adaptive( 300.dp ) ) {
            newsFeed(
                feedState = NewsFeedUiState.Loading,
            )
        }
    }
}

@Preview
@Preview( device = Devices.TABLET )
@Composable
private fun NewsFeedContentPreview(
    @PreviewParameter( UserNewsResourcePreviewParameterProvider::class )
    userNewsResources: List<UserNewsResource>
) {
    UpdateTheme {
        LazyVerticalGrid( columns = GridCells.Adaptive( 300.dp ) ) {
            newsFeed(
                feedState = NewsFeedUiState.Success( userNewsResources )
            )
        }
    }
}

@Preview
@Composable
fun TopicHeaderPreview(
    @PreviewParameter( UserNewsResourcePreviewParameterProvider::class )
    userNewsResources: List<UserNewsResource>
) {
    UpdateTheme {
        TopicHeader(
            modifier = Modifier.padding( 16.dp ),
            followableTopic = userNewsResources[0].followableTopics[0],
        ) {}
    }
}