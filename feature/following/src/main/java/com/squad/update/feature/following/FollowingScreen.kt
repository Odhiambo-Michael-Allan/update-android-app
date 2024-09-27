package com.squad.update.feature.following

import android.graphics.Paint.Align
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.squad.update.core.designsystem.component.DynamicAsyncImage
import com.squad.update.core.designsystem.theme.UpdateTheme
import com.squad.update.core.model.data.UserNewsResource
import com.squad.update.core.ui.DevicePreviews
import com.squad.update.core.ui.PreviewParameterData
import com.squad.update.core.ui.UserNewsResourcePreviewParameterProvider

@Composable
internal fun FollowingRoute(
    modifier: Modifier = Modifier,
) {
    FollowingScreen(
        isSyncing = true,
        topicSelectionUiState = TopicSelectionUiState.Shown(
            topics = PreviewParameterData.newsResources.flatMap { news -> news.followableTopics }
                .distinctBy { it.topic.id }
        ),
        onTopicCheckedChanged = { _, _ -> },
        onTopicClick = {},
        saveFollowedTopics = {}
    )
}

@Composable
internal fun FollowingScreen(
    modifier: Modifier = Modifier,
    isSyncing: Boolean,
    topicSelectionUiState: TopicSelectionUiState,
    onTopicCheckedChanged: (String, Boolean ) -> Unit,
    onTopicClick: ( String ) -> Unit,
    saveFollowedTopics: () -> Unit,
) {
    val isTopicSelectionLoading = topicSelectionUiState is TopicSelectionUiState.Loading

    val state = rememberLazyStaggeredGridState()

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyVerticalStaggeredGrid(
            modifier = Modifier
                .testTag( "following:feed" ),
            columns = StaggeredGridCells.Adaptive( 300.dp ),
            contentPadding = PaddingValues( 16.dp ),
            horizontalArrangement = Arrangement.spacedBy( 16.dp ),
            state = state
        ) {
            topicSelection(
                topicSelectionUiState = topicSelectionUiState,
                onTopicCheckedChanged = onTopicCheckedChanged,
                saveFollowedTopics = saveFollowedTopics,
            )
        }
        AnimatedVisibility(
            visible = isSyncing || isTopicSelectionLoading,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight },
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight },
            ) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding( top = 8.dp )
            ) {
                Card(
                    modifier = Modifier
                        .align( Alignment.Center )
                        .size( 48.dp )
                        .padding( 4.dp ),
                    onClick = {},
                    shape = RoundedCornerShape( 100.dp ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align( Alignment.Center )
                                .size( 24.dp ),
                        )
                    }
                }
            }
        }
    }
}

/**
 * An extension on [LazyListScope] defining the topic selection portion of the following screen.
 * Depending on the [topicSelectionUiState], this might emit no items.
 */
private fun LazyStaggeredGridScope.topicSelection(
    modifier: Modifier = Modifier,
    topicSelectionUiState: TopicSelectionUiState,
    onTopicCheckedChanged: ( String, Boolean ) -> Unit,
    saveFollowedTopics: () -> Unit,
) {
    when ( topicSelectionUiState ) {
        TopicSelectionUiState.Loading,
            TopicSelectionUiState.LoadFailed,
            TopicSelectionUiState.NotShown
            -> Unit

        is TopicSelectionUiState.Shown -> {
            item( span = StaggeredGridItemSpan.FullLine, contentType = "topic-selection" ) {
                Column( modifier = modifier ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding( top = 24.dp ),
                        text = stringResource(
                            id = R.string.feature_following_topicselection_guidance_title
                        ),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 24.dp, end = 24.dp),
                        text = stringResource(
                            id = R.string.feature_following_topicselection_guidance_subtitle
                        ),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TopicSelection(
                        modifier = Modifier.padding( bottom = 8.dp ),
                        topicSelectionUiState = topicSelectionUiState,
                        onTopicCheckedChanged = onTopicCheckedChanged
                    )
                }
            }
        }
    }
}

@OptIn( ExperimentalLayoutApi::class )
@Composable
private fun TopicSelection(
    modifier: Modifier = Modifier,
    topicSelectionUiState: TopicSelectionUiState.Shown,
    onTopicCheckedChanged: (String, Boolean) -> Unit,
) {
    FlowRow (
        modifier = modifier
            .padding( 8.dp )
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy( 8.dp )
    ) {
        topicSelectionUiState.topics.forEach {
            SingleTopicButton(
                name = it.topic.name,
                topicId = it.topic.id,
                imageUrl = it.topic.imageUrl,
                isSelected = it.isFollowed,
                onClick = onTopicCheckedChanged
            )
        }
    }
}

@Composable
private fun SingleTopicButton(
    name: String,
    topicId: String,
    imageUrl: String,
    isSelected: Boolean,
    onClick: ( String, Boolean ) -> Unit,
) {
    FilterChip(
        selected = isSelected,
        onClick = {
            onClick( topicId, !isSelected )
        },
        label = {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall
            )
        },
        leadingIcon = {
            TopicIcon(
                imageUrl = imageUrl
            )
        }
    )
}

@Composable
fun TopicIcon(
    modifier: Modifier = Modifier,
    imageUrl: String,
) {
    DynamicAsyncImage(
        modifier = modifier
            .size( 32.dp ),
        placeholder = painterResource( id = R.drawable.feature_following_ic_icon_placeholder ),
        imageUrl = imageUrl,
        contentDescription = null,
    )
}

@DevicePreviews
@Composable
fun FollowingScreenTopicSelection(
    @PreviewParameter( UserNewsResourcePreviewParameterProvider::class )
    userNewsResources: List<UserNewsResource>
) {
    UpdateTheme {
        FollowingScreen(
            isSyncing = true,
            topicSelectionUiState = TopicSelectionUiState.Shown(
                topics = userNewsResources.flatMap { news -> news.followableTopics }
                    .distinctBy { it.topic.id }
            ),
            onTopicCheckedChanged = { _, _ -> },
            onTopicClick = {},
            saveFollowedTopics = {}
        )
    }
}

