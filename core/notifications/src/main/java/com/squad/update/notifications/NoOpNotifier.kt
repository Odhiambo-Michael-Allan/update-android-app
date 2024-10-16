package com.squad.update.notifications

import com.squad.update.core.model.data.NewsResource
import javax.inject.Inject

/**
 * Implementation of [Notifier] which does nothing. Useful for tests and previews.
 */
internal class NoOpNotifier @Inject constructor() : Notifier {
    override fun postNewNotifications( newsResources: List<NewsResource> ) = Unit
}