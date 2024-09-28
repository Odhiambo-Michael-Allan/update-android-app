package com.squad.update.feature.following

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squad.update.core.data.repository.UserDataRepository
import com.squad.update.core.data.repository.UserNewsResourceRepository
import com.squad.update.core.data.util.SyncManager
import com.squad.update.core.domain.GetFollowableTopicsUseCase
import com.squad.update.core.ui.NewsFeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingScreenViewModel @Inject constructor(
    syncManager: SyncManager,
    userNewsResourceRepository: UserNewsResourceRepository,
    getFollowableTopicsUseCase: GetFollowableTopicsUseCase,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    private val shouldShowTopicSelection: Flow<Boolean> =
        userDataRepository.userData.map { !it.shouldHideTopicSelection }

    val topicSelectionUiState: StateFlow<TopicSelectionUiState> =
        combine(
            shouldShowTopicSelection,
            getFollowableTopicsUseCase()
        ) { shouldShowTopicSelection, topics ->
            if ( shouldShowTopicSelection ) TopicSelectionUiState.Shown( topics = topics )
            else TopicSelectionUiState.NotShown
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = TopicSelectionUiState.Loading
        )

    val feedState: StateFlow<NewsFeedUiState> =
        userNewsResourceRepository.observeAllForFollowedTopics()
            .map( NewsFeedUiState::Success )
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed( 5_000 ),
                initialValue = NewsFeedUiState.Loading
            )

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = false
        )

    fun dismissTopicSelection() {
        viewModelScope.launch {
            userDataRepository.setShouldHideTopicSelection( true )
        }
    }

    fun updateTopicSelection( topicId: String, isChecked: Boolean ) {
        viewModelScope.launch {
            userDataRepository.setTopicIdFollowed( topicId, isChecked )
        }
    }
}