package com.squad.update.feature.following

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.update.core.designsystem.component.UpdateButton
import com.squad.update.core.designsystem.component.UpdateOutlinedButton
import com.squad.update.core.designsystem.theme.UpdateTheme
import com.squad.update.core.model.data.UserNewsResource
import com.squad.update.core.ui.DevicePreviews
import com.squad.update.core.ui.NewsFeedUiState
import com.squad.update.core.ui.TopicIcon
import com.squad.update.core.ui.UserNewsResourcePreviewParameterProvider
import com.squad.update.core.ui.newsFeed
import com.squad.update.core.ui.subListNonStrict

private const val MAX_NUMBER_OF_PREVIEW_TOPICS = 10

@Composable
internal fun FollowingScreen(
    modifier: Modifier = Modifier,
    viewModel: FollowingScreenViewModel = hiltViewModel(),
    onTopicClick: ( String ) -> Unit,
) {

    val topicSelectionUiState by viewModel.topicSelectionUiState.collectAsStateWithLifecycle()
    val feedState by viewModel.feedState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    FollowingScreen(
        isSyncing = isSyncing,
        newsFeedUiState = feedState,
        topicSelectionUiState = topicSelectionUiState,
        onTopicCheckedChanged = viewModel::updateTopicSelection,
        onTopicClick = onTopicClick,
        saveFollowedTopics = viewModel::dismissTopicSelection
    )
}

@Composable
internal fun FollowingScreen(
    modifier: Modifier = Modifier,
    isSyncing: Boolean,
    newsFeedUiState: NewsFeedUiState,
    topicSelectionUiState: TopicSelectionUiState,
    onTopicCheckedChanged: (String, Boolean ) -> Unit,
    onTopicClick: ( String ) -> Unit,
    saveFollowedTopics: () -> Unit,
) {
    val isTopicSelectionLoading = topicSelectionUiState is TopicSelectionUiState.Loading

    val state = rememberLazyGridState()

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .testTag( "following:feed" ),
            columns = GridCells.Adaptive( 300.dp ),
            horizontalArrangement = Arrangement.spacedBy( 16.dp ),
            state = state
        ) {
            newsFeed(
                feedState = newsFeedUiState,
            )
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
                    .padding(top = 8.dp)
            ) {
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                        .padding(4.dp),
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
                                .align(Alignment.Center)
                                .size(24.dp),
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
private fun LazyGridScope.topicSelection(
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
            item(
                span = {
                    GridItemSpan( maxLineSpan )
                },
                contentType = "topic-selection"
            ) {
                Column( modifier = modifier ) {
                    Spacer( modifier = Modifier.height( 16.dp ) )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 2.dp),
                        text = stringResource(
                            id = R.string.feature_following_topicselection_guidance_title
                        ),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 2.dp),
                        text = stringResource(
                            id = R.string.feature_following_topicselection_guidance_subtitle
                        ),
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer( modifier = Modifier.height( 8.dp ) )
                    TopicSelection(
                        modifier = Modifier.padding( 16.dp, 8.dp ),
                        topicSelectionUiState = topicSelectionUiState,
                        onTopicCheckedChanged = onTopicCheckedChanged
                    )
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        UpdateOutlinedButton(
                            modifier = Modifier.weight( 1f ),
                            onClick = { /*TODO*/ }
                        ) {
                            Text(
                                text = stringResource( id = R.string.feature_following_see_more_topics ),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        UpdateButton(
                            modifier = Modifier
                                .padding( horizontal = 8.dp ),
                            onClick = saveFollowedTopics,
                            enabled = topicSelectionUiState.isDismissible,
                            containerColor = if ( topicSelectionUiState.isDismissible ) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Unspecified
                            }
                        ) {
                            Text(
                                text = stringResource( id = R.string.feature_following_done ),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
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
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        topicSelectionUiState.topics.subListNonStrict( MAX_NUMBER_OF_PREVIEW_TOPICS ).forEach {
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
        modifier = Modifier.padding( 4.dp, 0.dp ),
        selected = isSelected,
        onClick = {
            onClick( topicId, !isSelected )
        },
        label = {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        },
        leadingIcon = {
            TopicIcon(
                imageUrl = imageUrl
            )
        }
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
            newsFeedUiState = NewsFeedUiState.Success(
                userNewsResources = userNewsResources
            ),
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

