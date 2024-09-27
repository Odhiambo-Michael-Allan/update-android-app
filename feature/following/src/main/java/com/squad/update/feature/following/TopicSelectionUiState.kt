package com.squad.update.feature.following

import android.adservices.topics.Topic
import com.squad.update.core.model.data.FollowableTopic

/**
 * A sealed hierarchy describing the onboarding state for the for you screen.
 */
sealed interface TopicSelectionUiState {
    /**
     * The topic selection state is loading.
     */
    data object Loading: TopicSelectionUiState

    /**
     * The topic selection state was unable to load.
     */
    data object LoadFailed : TopicSelectionUiState

    /**
     * There is no topic selection state.
     */
    data object NotShown : TopicSelectionUiState

    /**
     * There is a topic selection state, with the given list of topics.
     */
    data class Shown(
        val topics: List<FollowableTopic>
    ) : TopicSelectionUiState {
        /**
         * True if the topic selection can be dismissed.
         */
        val isDismissible: Boolean get() = topics.any { it.isFollowed }

    }
}