package com.squad.update.sync.test

import com.squad.update.core.data.util.SyncManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

internal class NeverSyncingSyncManager @Inject constructor() : SyncManager {
    override val isSyncing: Flow<Boolean> = flowOf( false )

    override fun requestSync() = Unit
}