package com.squad.update.core.model.data

/**
 * External data layer representation of an UPDATE Topic.
 */
data class Topic(
    val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val url: String,
    val imageUrl: String
)
