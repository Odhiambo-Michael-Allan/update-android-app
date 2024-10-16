package com.squad.update.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.squad.update.core.common.network.Dispatcher
import com.squad.update.core.common.network.UpdateDispatchers
import com.squad.update.core.data.Synchronizer
import com.squad.update.core.data.repository.NewsRepository
import com.squad.update.core.data.repository.TopicsRepository
import com.squad.update.core.datastore.ChangeListVersions
import com.squad.update.core.datastore.UpdatePreferencesDataSource
import com.squad.update.sync.initializers.SyncConstraints
import com.squad.update.sync.initializers.syncForegroundInfo
import com.squad.update.sync.status.SyncSubscriber
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Syncs the data layer by delegating to the appropriate repository instances with sync
 * functionality.
 */
@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val updatePreferences: UpdatePreferencesDataSource,
    private val topicRepository: TopicsRepository,
    private val newsRepository: NewsRepository,
    @Dispatcher( UpdateDispatchers.IO ) private val ioDispatcher: CoroutineDispatcher,
) : CoroutineWorker( appContext, workerParams ), Synchronizer {

    override suspend fun getForegroundInfo(): ForegroundInfo = appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext( ioDispatcher ) {

        // First sync the repositories in parallel
        val syncedSuccessfully = awaitAll(
            async { topicRepository.sync() },
            async { newsRepository.sync() }
        ).all { it }

        if ( syncedSuccessfully ) {
            Result.success()
        } else {
            Result.retry()
        }
    }

    override suspend fun getChangeListVersions(): ChangeListVersions =
        updatePreferences.getChangeListVersions()

    override suspend fun updateChangeListVersions(
        update: ChangeListVersions.() -> ChangeListVersions
    ) = updatePreferences.updateChangeListVersion( update )

    companion object {
        /**
         * Expedited one time work to sync data on app startup.
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited( OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST )
            .setConstraints( SyncConstraints )
            .setInputData( SyncWorker::class.delegatedData() )
            .build()
    }
}