package com.squad.update.notifications

import com.squad.update.core.model.data.NewsResource

/**
 * Interface for creating notifications in the app.
 */
interface Notifier {
    fun postNewNotifications( newsResources: List<NewsResource> )
}