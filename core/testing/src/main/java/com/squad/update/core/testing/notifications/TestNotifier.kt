package com.squad.update.core.testing.notifications

import com.squad.update.core.model.data.NewsResource
import com.squad.update.notifications.Notifier

/**
 * Aggregates news resources that have been notified for addition.
 */
class TestNotifier : Notifier {

    private val mutableAddedNewResources = mutableListOf<List<NewsResource>>()

    val addedNewsResources: List<List<NewsResource>> = mutableAddedNewResources

    override fun postNewNotifications( newsResources: List<NewsResource> ) {
        mutableAddedNewResources.add( newsResources )
    }
}