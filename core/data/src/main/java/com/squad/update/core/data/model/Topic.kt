package com.squad.update.core.data.model

import com.squad.update.core.database.model.TopicEntity
import com.squad.update.core.network.model.NetworkTopic

fun NetworkTopic.asEntity() = TopicEntity(
    id = id,
    name = name,
    shortDescription = shortDescription,
    longDescription = longDescription,
    url = url,
    imageUrl = imageUrl
)