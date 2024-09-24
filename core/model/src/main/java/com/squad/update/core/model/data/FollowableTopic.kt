package com.squad.update.core.model.data

/**
 * A [Topic] with the additional information for whether or not it is followed.
 */
data class FollowableTopic(
    val topic: Topic,
    val isFollowed: Boolean,
)
