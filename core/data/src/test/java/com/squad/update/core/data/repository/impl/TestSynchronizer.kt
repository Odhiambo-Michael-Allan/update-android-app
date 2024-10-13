package com.squad.update.core.data.repository.impl

import com.squad.update.core.data.Synchronizer
import com.squad.update.core.datastore.ChangeListVersions
import com.squad.update.core.datastore.UpdatePreferencesDataSource

/**
 * Test synchronizer that delegates to [UpdatePreferencesDataSource]
 */
class TestSynchronizer(
    private val updatePreferences: UpdatePreferencesDataSource
) : Synchronizer {

    override suspend fun getChangeListVersions(): ChangeListVersions =
        updatePreferences.getChangeListVersions()

    override suspend fun updateChangeListVersions(
        update: ChangeListVersions.() -> ChangeListVersions
    ) = updatePreferences.updateChangeListVersion( update )
}